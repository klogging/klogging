/*

   Copyright 2021-2024 Michael Strasser.

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
import io.klogging.randomString
import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RenderPattern
import io.klogging.sending.EventSender
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
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
        it("if `eventSender` is present, it overrides any specified render and sender") {
            val sinkConfig = FileSinkConfiguration(eventSender = TestEventSender::class.qualifiedName!!)
                .toSinkConfiguration()
            sinkConfig.shouldNotBeNull()
            sinkConfig.eventSender.shouldBeInstanceOf<TestEventSender>()
        }
        it("if `renderPattern` is present, it overrides `renderWith`") {
            val sinkConfig = FileSinkConfiguration(renderWith = "RENDER_ANSI", renderPattern = "%m", sendTo = "STDOUT")
                .toSinkConfiguration()
            sinkConfig.shouldNotBeNull()
            sinkConfig.renderer.shouldNotBeNull()
            sinkConfig.renderer shouldNotBe RENDER_ANSI
            sinkConfig.renderer.shouldBeInstanceOf<RenderPattern>()
        }

    }
})

class TestEventSender : EventSender {
    override fun invoke(batch: List<LogEvent>) {}
}
