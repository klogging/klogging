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

package io.klogging.config

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.WARN
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

internal class LevelRangeTest : DescribeSpec({
    describe("LevelRange") {
        it("is also a Kotlin range") {
            LevelRange(DEBUG, WARN).shouldBeInstanceOf<ClosedRange<Level>>()
        }
        it("ensures the minLevel value is not greater than the maxLevel value") {
            LevelRange(WARN, DEBUG) shouldBe LevelRange(DEBUG, WARN)
        }
    }
})
