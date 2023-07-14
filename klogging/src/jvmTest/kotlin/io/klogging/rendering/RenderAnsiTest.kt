/*

   Copyright 2021-2023 Michael Strasser.

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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RenderAnsiTest : DescribeSpec({

    describe("shortenName() function") {
        it("keeps a short name as it is") {
            shortenName("main", 20) shouldBe "main"
        }
        it("shortens dotted parts of class names") {
            shortenName("io.klogging.events.LogEvent", 20) shouldBe "i.k.events.LogEvent"
        }
        it("shortens complex thread names") {
            shortenName("DefaultDispatcher-worker-3+Playpen") shouldBe "D-worker-3+Playpen"
        }
        it("ignores consecutive delimiters") {
            shortenName("OkHttp http://localhost:4317/...") shouldBe "O h://l:4317/..."
        }
        it("truncates a single string without delimiters") {
            shortenName("Triantiwontigongalope") shouldBe "Triantiwontigongalop"
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
})
