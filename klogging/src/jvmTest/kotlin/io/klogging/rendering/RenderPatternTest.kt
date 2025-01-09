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
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class RenderPatternTest : DescribeSpec({
    describe("RenderPattern") {
        it("renders message and newline only by default") {
            val event = logEvent()
            RenderPattern()(event) shouldBe event.message
        }
        it("renders message only if %m specified") {
            val event = logEvent()
            RenderPattern("%m")(event) shouldBe event.message
        }
        it("renders complex messages") {
            val event = logEvent(context = "main")
            RenderPattern("%t %v [%c] : %l : %m : %i%n")(event) shouldBe
                    "${event.timestamp} ${event.level} [${event.context}] : " +
                    "${event.logger} : ${event.message} : \n"
        }
        it("renders string sections as provided") {
            RenderPattern("Hello")(logEvent()) shouldBe "Hello"
        }
        describe("rendering log timestamps") {
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
        describe("rendering hostname") {
            val event = logEvent(host = "com.example.TestLogger")
            it("includes full hostname if no length is specified") {
                RenderPattern("%h")(event) shouldBe "com.example.TestLogger"
            }
            it("left-aligns the hostname if a positive width is specified") {
                RenderPattern("%25h")(event) shouldBe "com.example.TestLogger   "
            }
            it("right-aligns the hostname if a negative width is specified") {
                RenderPattern("%-25h")(event) shouldBe "   com.example.TestLogger"
            }
            it("shortens hostname if the specified width is shorter") {
                RenderPattern("%15h")(event) shouldBe "c.e.TestLogger "
                RenderPattern("%-15h")(event) shouldBe " c.e.TestLogger"
            }
        }
        describe("rendering log context") {
            val event = logEvent(context = "DefaultDispatcher-worker-10")
            it("includes full context if no length is specified") {
                RenderPattern("%c")(event) shouldBe "DefaultDispatcher-worker-10"
            }
            it("left-aligns the context if a positive width is specified") {
                RenderPattern("%30c")(event) shouldBe "DefaultDispatcher-worker-10   "
            }
            it("right-aligns the context if a negative width is specified") {
                RenderPattern("%-30c")(event) shouldBe "   DefaultDispatcher-worker-10"
            }
            it("shortens context if the specified width is shorter") {
                RenderPattern("%15c")(event) shouldBe "D-worker-10    "
                RenderPattern("%-15c")(event) shouldBe "    D-worker-10"
            }
        }
        describe("rendering logger name") {
            val event = logEvent(logger = "com.example.TestLogger")
            it("includes full logger name if no length is specified") {
                RenderPattern("%l")(event) shouldBe "com.example.TestLogger"
            }
            it("left-aligns the logger name if a positive width is specified") {
                RenderPattern("%25l")(event) shouldBe "com.example.TestLogger   "
            }
            it("right-aligns the logger name if a negative width is specified") {
                RenderPattern("%-25l")(event) shouldBe "   com.example.TestLogger"
            }
            it("shortens logger name if the specified width is shorter") {
                RenderPattern("%15l")(event) shouldBe "c.e.TestLogger "
                RenderPattern("%-15l")(event) shouldBe " c.e.TestLogger"
            }
        }
        describe("rendering log level") {
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
        describe("rendering message") {
            it("outputs simple messages") {
                RenderPattern("%m")(logEvent(message = "Start of test")) shouldBe "Start of test"
            }
            it("evaluates message templates with provided items") {
                RenderPattern("%m")(logEvent(
                    message = "User {id} signed in",
                    items = mapOf("id" to "8463782123")
                )) shouldBe "User 8463782123 signed in"
            }
        }
        describe("rendering stack trace") {
            val stackTrace = "line one\nline two\nline three"
            val event = logEvent(stackTrace = stackTrace)
            it("outputs nothing if there is no stack trace") {
                RenderPattern("%s")(logEvent()) shouldBe ""
            }
            it("starts a stack trace on a new line") {
                RenderPattern("%s")(event) shouldBe "\n$stackTrace"
            }
            it("only outputs the max number of lines specified") {
                RenderPattern("%1s")(event) shouldBe "\nline one"
                RenderPattern("%2s")(event) shouldBe "\nline one\nline two"
                RenderPattern("%3s")(event) shouldBe "\nline one\nline two\nline three"
                RenderPattern("%4s")(event) shouldBe "\nline one\nline two\nline three"
            }
            it("ignores zero or negative number of lines") {
                RenderPattern("%s")(event) shouldBe "\n$stackTrace"
                RenderPattern("%0s")(event) shouldBe "\n$stackTrace"
                RenderPattern("%-3s")(event) shouldBe "\n$stackTrace"
            }
        }
        describe("rendering context items") {
            it("outputs empty braces if there are no items") {
                RenderPattern("Items: %i")(logEvent()) shouldBe "Items: "
            }
            it("outputs items in braces if present") {
                RenderPattern("Items: %i")(logEvent(
                    items = mapOf("name" to "Test", "id" to "id_1357908642")
                )) shouldBe "Items: {name=Test, id=id_1357908642}"
            }
        }
        it("renders newline tokens as newlines") {
            RenderPattern("%n")(logEvent()) shouldBe "\n"
        }
    }
})
