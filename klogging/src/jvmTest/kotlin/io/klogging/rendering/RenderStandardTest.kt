/*

   Copyright 2024 Baris Ceviz.

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

package io.klogging.rendering

import io.klogging.Level.INFO
import io.klogging.events.LogEvent
import io.klogging.events.threadContext
import io.klogging.events.timestampNow
import io.klogging.randomString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RenderStandardTest : DescribeSpec({
    describe("Render a `LogEvent` to STANDARD") {
        it("omits stackTrace if `stackTrace` is null") {
            val ts = timestampNow()
            val event = LogEvent(
                randomString(), ts, "test.local", "Test", threadContext(), INFO,
                null, "Message", null, mapOf(),
            )

            RENDER_STANDARD(event) shouldBe """{
            |"timestamp":"${event.timestamp}",
            |"level":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"context":"${event.context}",
            |"message":"${event.message}"
            |}
            """.trimMargin().replace("\n", "")
        }
        it("includes stackTrace if `stackTrace` is present") {
            val ts = timestampNow()
            val trace = randomString()
            val event =
                LogEvent(
                    randomString(), ts, "test.local", "Test", threadContext(), INFO,
                    null, "Message", trace, mapOf(),
                )

            RENDER_STANDARD(event) shouldBe """{
            |"timestamp":"${event.timestamp}",
            |"level":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"context":"${event.context}",
            |"message":"${event.message}",
            |"stackTrace":"${event.stackTrace}"
            |}
            """.trimMargin().replace("\n", "")
        }
        it("includes message") {
            val ts = timestampNow()
            val event = LogEvent(
                randomString(), ts, "test.local", "Test", threadContext(), INFO,
                null, "Message", null, mapOf(),
            )

            RENDER_STANDARD(event) shouldBe """{
            |"timestamp":"${event.timestamp}",
            |"level":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"context":"${event.context}",
            |"message":"${event.message}"
            |}
            """.trimMargin().replace("\n", "")
        }
        it("omits context if the event `context` is null") {
            val ts = timestampNow()
            val event = LogEvent(
                randomString(), ts, "test.local", "Test", null, INFO,
                null, "Message", null, mapOf(),
            )

            RENDER_STANDARD(event) shouldBe """{
            |"timestamp":"${event.timestamp}",
            |"level":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"message":"${event.message}"
            |}
            """.trimMargin().replace("\n", "")
        }
        it("evaluates templated items into the message") {
            val ts = timestampNow()
            val id = randomString()
            val event = LogEvent(
                randomString(), ts, "test.local", "Test", null, INFO,
                "ID is {id}", "ID is {id}", null, mapOf("id" to id),
            )

            RENDER_STANDARD(event) shouldBe """{
            |"timestamp":"${event.timestamp}",
            |"level":"${event.level}",
            |"host":"${event.host}",
            |"logger":"${event.logger}",
            |"id":"$id",
            |"message":"${event.evalTemplate()}"
            |}
            """.trimMargin().replace("\n", "")
        }
    }
})
