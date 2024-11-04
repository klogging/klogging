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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class StringRenderingTest : DescribeSpec({
    describe("CharSequence.padRight() extension function") {
        it("fills a string as it is") {
            "main".padRight(4) shouldBe "main"
        }
        it("pads a string as required") {
            "main".padRight(5) shouldBe " main"
            "main".padRight(6) shouldBe "  main"
            "main".padRight(20) shouldBe "                main"
        }
        it ("truncates a string if it is too long") {
            "wobbegong".padRight(4) shouldBe "gong"
        }
    }
    describe("CharSequence.right() extension function") {
        it("fills a string as it is") {
            "main".right(4) shouldBe "main"
        }
        it("pads a short string as required") {
            "main".right(8) shouldBe "    main"
        }
        it("truncates a string if it is too long") {
            "wobbegong".right(5) shouldBe "wobbe"
        }
        it("breaks up using delimiters if it is too long") {
            "org.apache.commons.lang3.StringBuilder".right(10) shouldBe "ingBuilder"
            "org.apache.commons.lang3.StringBuilder".right(24) shouldBe "   o.a.c.l.StringBuilder"
            "org.apache.commons.lang3.StringBuilder".right20 shouldBe "o.a.c.l.StringBuilde"
        }
    }
})