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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class ShortenNameTest : DescribeSpec({
    describe("CharSequence.shortenName() extension function") {
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
        it("truncates a string to 5 if a max width less than 5 is provided") {
            checkAll(Arb.int(max = MINIMUM_MAX_WIDTH - 1)) { invalidWidth ->
                "endEpochNanos=1704182935159253928}".shortenName(invalidWidth) shouldBe "endEp"
            }
        }
        it("truncates a string to widths greater than 5") {
            val nameSource = "A".repeat(200)
            checkAll(Arb.int(min = MINIMUM_MAX_WIDTH, max = 200)) { width ->
                nameSource.shortenName(width) shouldBe nameSource.substring(0, width)
            }
        }
    }
})