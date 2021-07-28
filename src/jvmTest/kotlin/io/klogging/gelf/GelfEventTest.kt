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

package io.klogging.gelf

import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.randomString
import io.klogging.timestampNow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class GelfEventTest : DescribeSpec({
    describe("Creating GELF event JSON") {
        it("includes logger name as _logger") {
            val ts = timestampNow()
            val event = LogEvent(ts, "test.local", "Test", Level.INFO, null, "Message", null, mapOf())

            event.toGelf() shouldBe """{
                |"version":"1.1",
                |"host":"${event.host}",
                |"short_message":"${event.message}",
                |"timestamp":${ts.graylogFormat()},
                |"level":${graylogLevel(Level.INFO)},
                |"_logger":"${event.logger}"
                |}""".trimMargin().replace("\n", "")
        }
        it("includes full_message with `stackTrace` if present") {
            val ts = timestampNow()
            val trace = randomString()
            val event = LogEvent(ts, "test.local", "Test", Level.INFO, null, "Message", trace, mapOf())

            event.toGelf() shouldBe """{
                |"version":"1.1",
                |"host":"${event.host}",
                |"short_message":"${event.message}",
                |"full_message":"$trace",
                |"timestamp":${ts.graylogFormat()},
                |"level":${graylogLevel(Level.INFO)},
                |"_logger":"${event.logger}"
                |}""".trimMargin().replace("\n", "")
        }
    }
})
