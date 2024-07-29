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

import io.klogging.logEvent
import io.klogging.randomString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ConsoleRenderingTest : DescribeSpec({

    describe("shortenName() extension function") {
        it("keeps a short name as it is") {
            "main".shortenName(20) shouldBe "main"
        }
        it("shortens dotted parts of class names") {
            "io.klogging.events.LogEvent".shortenName(20) shouldBe "i.k.events.LogEvent"
        }
        it("shortens complex thread names") {
            "DefaultDispatcher-worker-3+Playpen".shortenName() shouldBe "D-worker-3+Playpen"
        }
        it("ignores consecutive delimiters") {
            "OkHttp http://localhost:4317/...".shortenName() shouldBe "O h://l:4317/..."
        }
        it("truncates a single string without delimiters") {
            "Triantiwontigongalope".shortenName() shouldBe "Triantiwontigongalop"
        }
        it("truncates a string to 20 if a max width less than 5 is provided") {
            checkAll(Arb.int(max = MINIMUM_MAX_WIDTH - 1)) { invalidWidth ->
                "endEpochNanos=1704182935159253928}".shortenName(invalidWidth) shouldBe "endEpochNanos=170418"
            }
        }
        it("truncates a string to widths greater than 5") {
            val nameSource = "A".repeat(200)
            checkAll(Arb.int(min = MINIMUM_MAX_WIDTH, max = 200)) { width ->
                nameSource.shortenName(width) shouldBe nameSource.substring(0, width)
            }
        }
    }

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
            logEvent().itemsAndStackTrace shouldBe ""
        }
        it("returns ' : ' followed by the list of items if present") {
            val items = mapOf(randomString() to randomString(), randomString() to randomString())
            logEvent(items = items).itemsAndStackTrace shouldBe " : $items"
        }
        it("returns a newline followed by the stack trace if present") {
            val stackTrace = randomString()
            logEvent(stackTrace = stackTrace).itemsAndStackTrace shouldBe "\n$stackTrace"
        }
    }
})