/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.rendering

import io.klogging.Level
import io.klogging.events.LogEvent
import io.klogging.randomString
import io.klogging.timestampNow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RenderSimpleTest : DescribeSpec({
    describe("Render a `LogEvent` to a simple string") {
        it("omits null stack trace") {
            val ts = timestampNow()
            val event = LogEvent(
                randomString(), ts, "test.local", "Test", "test-thread", Level.INFO,
                null, "Message", null, mapOf()
            )

            RENDER_SIMPLE(event) shouldBe "${ts.localString} INFO [test-thread] Test : Message"
        }

        it("includes items only if they are present") {
            val ts = timestampNow()
            val event = LogEvent(
                randomString(), ts, "test.local", "Test", "test-thread", Level.WARN,
                null, "Message", null, mapOf("colour" to "green")
            )

            RENDER_SIMPLE(event) shouldBe "${ts.localString} WARN [test-thread] Test : Message : {colour=green}"
        }

        it("puts a stack trace starting on the next line") {
            val ts = timestampNow()
            val stackTrace = "${randomString()}\n${randomString()}\n${randomString()}"
            val event = LogEvent(
                randomString(), ts, "test.local", "Test", "test-thread", Level.INFO,
                null, "Message", stackTrace, mapOf()
            )

            RENDER_SIMPLE(event) shouldBe "${ts.localString} INFO [test-thread] Test : Message\n$stackTrace"
        }
    }
})
