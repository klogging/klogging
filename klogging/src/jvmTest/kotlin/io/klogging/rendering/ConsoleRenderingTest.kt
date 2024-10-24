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
import io.klogging.randomString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum

class ConsoleRenderingTest : DescribeSpec({

    describe("right20 extension property") {
        it("right-aligns a short name") {
            "main".right20 shouldBe "                main"
        }
        it("right-aligns a longer name") {
            "io.klogging.Klogging".right20 shouldBe "io.klogging.Klogging"
        }
        it("shortens package names in a too-long name") {
            "io.klogging.events.LogEvent".right20 shouldBe " i.k.events.LogEvent"
        }
    }

    describe("itemsAndStackTrace extension property") {
        it("returns an empty string if there are no items or stack trace") {
            logEvent(message = randomString()).itemsAndStackTrace shouldBe ""
        }
        it("returns ' : ' followed by the list of items if present") {
            val items = mapOf(randomString() to randomString(), randomString() to randomString())
            logEvent(items = items).itemsAndStackTrace shouldBe " : $items"
        }
        it("returns a newline followed by the stack trace if present") {
            val stackTrace = randomString()
            logEvent(stackTrace = stackTrace).itemsAndStackTrace shouldBe "\n$stackTrace"
        }
        it("returns both items and stack trace if both are present") {
            val items = mapOf(randomString() to randomString(), randomString() to randomString())
            val stackTrace = randomString()
            logEvent(items = items, stackTrace = stackTrace).itemsAndStackTrace shouldBe " : $items\n$stackTrace"
        }
    }

    describe("Level.rpad() extension function returns a Level name in the width provided") {
        describe("sets a minimum width of 1") {
            checkAll(Exhaustive.enum<Level>(), Arb.int(max = 0)) { level, width ->
                level.rpad(width) shouldBe level.toString().take(1)
            }
        }
        describe("uses the first characters of the level name if width is shorter") {
            checkAll(Exhaustive.enum<Level>(), Arb.int(1, 4)) { level, width ->
                level.rpad(width) shouldBe level.toString().take(width)
            }
        }
        describe("right-pads the level name if the width is greater") {
            checkAll(Exhaustive.enum<Level>(), Arb.int(5, 100)) { level, width ->
                val padding = " ".repeat(width - level.toString().length)
                level.rpad(width) shouldBe padding + level.toString()
            }
        }
    }
})