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

package io.klogging.rendering

import io.klogging.Level
import io.klogging.logEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldEndWith
import kotlinx.datetime.Instant

class RenderAnsiTest : DescribeSpec({
    describe("`renderAnsi()` function") {
        val event = logEvent(
            timestamp = Instant.parse("2024-11-03T00:08:42.123456Z"),
            level = Level.DEBUG,
            context = "DefaultDispatcher-worker-5",
            logger = "com.example.Thing",
            message = "Test"
        )
        // Use `shouldEndWith` to avoid issues with different timezones.
        it("renders standard widths") {
            renderAnsi(contextWidth = 20, loggerWidth = 20)(event) shouldEndWith
                    "DEBUG [          D-worker-5] :    com.example.Thing : Test"
        }
        it("renders adjusted widths") {
            renderAnsi(contextWidth = 10, loggerWidth = 30)(event) shouldEndWith
                    "DEBUG [D-worker-5] :              com.example.Thing : Test"
        }
        it("omits context if contextWidth is zero") {
            renderAnsi(contextWidth = 0, loggerWidth = 20)(event) shouldEndWith
                    "DEBUG :    com.example.Thing : Test"
        }
        it("omits logger name if loggerWidth is zero") {
            renderAnsi(contextWidth = 10, loggerWidth = 0)(event) shouldEndWith
                    "DEBUG [D-worker-5] : Test"
        }
        it("includes any context items") {
            renderAnsi(0, 0)(event.copy(items = mapOf("this" to "that"))) shouldEndWith
                    "DEBUG : Test : {this=that}"
        }
        it("includes any stacktrace") {
            renderAnsi(0, 0)(
                event.copy(
                    stackTrace = "io.klogging.PretendException: message\n    at io.klogging.test.BlahTest.blah"
                )
            ) shouldEndWith
                    "DEBUG : Test\nio.klogging.PretendException: message\n" +
                    "    at io.klogging.test.BlahTest.blah"
        }
    }
})
