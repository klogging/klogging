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

package io.klogging.config

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class FileConfigurationTest : DescribeSpec({
    describe("`FileLevelRange.toLevelRange` function") {
        it("sets full range if no levels are set") {
            FileLevelRange().toLevelRange() shouldBe LevelRange(TRACE, FATAL)
        }
        it("sets single level if only `atLevel` is set") {
            FileLevelRange(
                atLevel = INFO,
            ).toLevelRange() shouldBe LevelRange(INFO, INFO)
        }
        it("sets single level from `atLevel` if any other levels are also set") {
            FileLevelRange(
                atLevel = INFO,
                fromMinLevel = DEBUG,
            ).toLevelRange() shouldBe LevelRange(INFO, INFO)
            FileLevelRange(
                atLevel = INFO,
                toMaxLevel = ERROR,
            ).toLevelRange() shouldBe LevelRange(INFO, INFO)
        }
        it("sets lower bound if only `fromMinLevel` is set") {
            FileLevelRange(
                fromMinLevel = INFO,
            ).toLevelRange() shouldBe LevelRange(INFO, FATAL)
        }
        it("sets upper bound if only `toMaxLevel` is set") {
            FileLevelRange(
                toMaxLevel = INFO,
            ).toLevelRange() shouldBe LevelRange(TRACE, INFO)
        }
        it("sets a range if `fromMinLevel` and `toMaxLevel` are set") {
            FileLevelRange(
                fromMinLevel = INFO,
                toMaxLevel = WARN,
            ).toLevelRange() shouldBe LevelRange(INFO, WARN)
        }
    }
})
