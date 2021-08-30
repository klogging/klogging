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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class RenderAnsiTest : DescribeSpec({

    describe("compressLoggerName() function") {
        it("keeps a short name as it is") {
            compressLoggerName("main") shouldBe "main"
        }
        it("shortens dotted parts of names") {
            compressLoggerName("io.klogging.events.LogEvent") shouldBe "i.k.e.LogEvent"
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
            "io.klogging.events.LogEvent".right20 shouldBe "      i.k.e.LogEvent"
        }
    }
})
