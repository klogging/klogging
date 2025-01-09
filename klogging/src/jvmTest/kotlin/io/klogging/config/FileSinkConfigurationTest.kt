/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.config

import io.klogging.events.LogEvent
import io.klogging.logEvent
import io.klogging.randomString
import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RenderPattern
import io.klogging.rendering.RenderString
import io.klogging.sending.EventSender
import io.klogging.sending.STDOUT
import io.klogging.sending.SendString
import io.klogging.sending.SplunkEndpoint
import io.klogging.sending.SplunkHec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldInclude
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf

class FileSinkConfigurationTest : DescribeSpec({
    describe("`FileSinkConfiguration`") {
        it("`toString()` masks `apiKey` values to ******** if present") {
            val key = randomString()
            val config = FileSinkConfiguration(renderWith = "RENDER_CLEF", apiKey = key)
            with(config.toString()) {
                shouldNotContain(key)
                shouldContain("apiKey=********")
            }
        }
        describe("`toSinkConfiguration` function") {
            it("returns null if no renderer or sender is specified") {
                FileSinkConfiguration().toSinkConfiguration() shouldBe null
            }
            it("if `eventSender` is present, it overrides any specified render and sender") {
                val sinkConfig = FileSinkConfiguration(eventSender = TestEventSender::class.qualifiedName!!)
                    .toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.eventSender.shouldBeInstanceOf<TestEventSender>()
            }
            it("if `renderPattern` is present, it overrides `renderWith`") {
                val sinkConfig =
                    FileSinkConfiguration(renderWith = "RENDER_ANSI", renderPattern = "%m", sendTo = "STDOUT")
                        .toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.renderer.shouldNotBeNull()
                sinkConfig.renderer shouldNotBe RENDER_ANSI
                sinkConfig.renderer.shouldBeInstanceOf<RenderPattern>()
            }
            it("if `renderWith` names a built-in renderer, it is used for rendering") {
                val sinkConfig = FileSinkConfiguration(renderWith = "RENDER_ANSI", sendTo = "STDOUT")
                    .toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.renderer shouldBe RENDER_ANSI
            }
            it("if `renderWith` names a `RenderString` class, it is used for rendering") {
                val sinkConfig =
                    FileSinkConfiguration(renderWith = TestRenderString::class.qualifiedName!!, sendTo = "STDOUT")
                        .toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.renderer(logEvent()) shouldBe "TestRenderString"
            }
            it("if `renderWith` is not named but `renderHec` is, the latter is used") {
                val index = randomString()
                val sinkConfig = FileSinkConfiguration(renderHec = RenderHec(index = index), sendTo = "STDOUT")
                    .toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.renderer(logEvent()).shouldInclude(""""index":"$index"""")
            }
            it("if `splunkServer` is present, it overrides all other configuration") {
                val sinkConfig = FileSinkConfiguration(
                    splunkServer = SplunkEndpoint(
                        hecUrl = "http://localhost:8000",
                        hecToken = "TEST_TOKEN",
                    )
                ).toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.eventSender.shouldNotBeNull()
                sinkConfig.eventSender.shouldBeInstanceOf<SplunkHec>()
            }
            it("if `sendTo` names a built-in sender, it is used for sending") {
                val sinkConfig = FileSinkConfiguration(renderWith = "RENDER_SIMPLE", sendTo = "STDOUT")
                    .toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.stringSender shouldBe STDOUT
            }
            it("if `sendTo` names a `SendString` class, it is used for sending") {
                val sinkConfig = FileSinkConfiguration(
                    renderWith = "RENDER_SIMPLE",
                    sendTo = TestStringSender::class.qualifiedName!!
                ).toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.stringSender.shouldBeInstanceOf<TestStringSender>()
            }
            it("else if `seqServer` is present, it overrides other configuration") {
                val sinkConfig = FileSinkConfiguration(seqServer = "http://localhost:5341")
                    .toSinkConfiguration()
                sinkConfig.shouldNotBeNull()
                sinkConfig.renderer shouldBe RENDER_CLEF
            }
        }
    }
})

class TestEventSender : EventSender {
    override fun invoke(batch: List<LogEvent>) {}
}

class TestRenderString : RenderString {
    override fun invoke(event: LogEvent): String = "TestRenderString"
}

class TestStringSender : SendString {
    override fun invoke(eventString: String) {}
}
