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

package io.klogging.rendering

import io.klogging.Level
import io.klogging.logEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class RenderPatternTest : DescribeSpec({
    describe("RenderPattern") {
        it("renders message and newline only by default") {
            val event = logEvent()
            RenderPattern()(event) shouldBe "${event.message}\n"
        }
        it("renders message only if %m specified") {
            val event = logEvent()
            RenderPattern("%m")(event) shouldBe event.message
        }
        it("renders complex messages") {
            val event = logEvent()
            RenderPattern("%t %v [%c] : %l : %m : %i%n")(event) shouldBe
                    "${event.timestamp} ${event.level} [${event.context}] : " +
                    "${event.logger} : ${event.message} : ${event.items}\n"
        }
        describe("rendering log event levels") {
            it("left-aligns fields with positive widths") {
                val event = logEvent(level = Level.DEBUG)
                RenderPattern("%0v")(event) shouldBe "DEBUG"
                RenderPattern("%1v")(event) shouldBe "D"
                RenderPattern("%2v")(event) shouldBe "DE"
                RenderPattern("%3v")(event) shouldBe "DEB"
                RenderPattern("%4v")(event) shouldBe "DEBU"
                RenderPattern("%5v")(event) shouldBe "DEBUG"
                RenderPattern("%6v")(event) shouldBe "DEBUG "
            }
            it("right-aligns fields with negative widths") {
                val event = logEvent(level = Level.DEBUG)
                RenderPattern("%0v")(event) shouldBe "DEBUG"
                RenderPattern("%-1v")(event) shouldBe "D"
                RenderPattern("%-2v")(event) shouldBe "DE"
                RenderPattern("%-3v")(event) shouldBe "DEB"
                RenderPattern("%-4v")(event) shouldBe "DEBU"
                RenderPattern("%-5v")(event) shouldBe "DEBUG"
                RenderPattern("%-6v")(event) shouldBe " DEBUG"
            }
            it("formats levels in different colours with {COLOR} or {COLOUR} format") {
                RenderPattern("%v{COLOUR}")(logEvent(level = Level.TRACE)) shouldBe grey("TRACE")
                RenderPattern("%v{COLOR}")(logEvent(level = Level.DEBUG)) shouldBe "DEBUG"
                RenderPattern("%v{COLOUR}")(logEvent(level = Level.INFO)) shouldBe green("INFO")
                RenderPattern("%v{COLOR}")(logEvent(level = Level.WARN)) shouldBe yellow("WARN")
                RenderPattern("%v{COLOUR}")(logEvent(level = Level.ERROR)) shouldBe red("ERROR")
                RenderPattern("%v{COLOR}")(logEvent(level = Level.FATAL)) shouldBe brightRed("FATAL")
            }
            it("combines widths and colours") {
                RenderPattern("%5v{COLOUR}")(logEvent(level = Level.TRACE)) shouldBe grey("TRACE")
                RenderPattern("%-5v{COLOR}")(logEvent(level = Level.DEBUG)) shouldBe "DEBUG"
                RenderPattern("%5v{COLOUR}")(logEvent(level = Level.INFO)) shouldBe green("INFO ")
                RenderPattern("%-5v{COLOR}")(logEvent(level = Level.WARN)) shouldBe yellow(" WARN")
                RenderPattern("%5v{COLOUR}")(logEvent(level = Level.ERROR)) shouldBe red("ERROR")
                RenderPattern("%-5v{COLOR}")(logEvent(level = Level.FATAL)) shouldBe brightRed("FATAL")
            }
        }
        describe("rendering log event timestamps") {
            val event = logEvent(timestamp = Instant.parse("2024-12-14T10:08:42.123456Z"))
            it("uses ISO8601 format by default") {
                RenderPattern("%t")(event) shouldBe "2024-12-14T10:08:42.123456Z"
            }
            it("shows local time with LOCAL_TIME format") {
                RenderPattern("%t{LOCAL_TIME}")(event) shouldBe event.timestamp.localTime
            }
            it("uses the specified length") {
                RenderPattern("%10t")(event) shouldBe "2024-12-14"
            }
        }
    }
})
