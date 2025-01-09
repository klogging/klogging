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

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import io.klogging.fixturePath
import io.klogging.rendering.RenderString
import io.klogging.sending.SendString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf

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
    describe("`loadByClassName()` function") {
        it("instantiates a class from the classpath by its name") {
            val testRenderer = loadByClassName<RenderString>("io.klogging.config.TestRenderer")
            testRenderer.shouldBeInstanceOf<RenderString>()
            testRenderer.shouldBeTypeOf<TestRenderer>()
        }
        it("finds an object from the classpath by its name") {
            val testRenderer = loadByClassName<SendString>("io.klogging.config.DoNothingSender")
            testRenderer.shouldBeInstanceOf<SendString>()
            testRenderer.shouldBeTypeOf<DoNothingSender>()
        }
    }
    describe("`findConfigFile()` function`") {
        it("uses a specified path if the file exists") {
            val path = fixturePath("klogging-test.json")
            val configFile = findConfigFile(path)
            configFile.shouldNotBeNull()
            configFile.path shouldBe path
        }
        it("uses a path specified in the `KLOGGING_CONFIG_PATH` environment variable") {
            val path = fixturePath("klogging-test.json")
            withEnvironment(ENV_KLOGGING_CONFIG_PATH, path) {
                val configFile = findConfigFile()
                configFile.shouldNotBeNull()
                configFile.path shouldBe path
            }
        }
        it("finds `klogging.json` on the classpath") {
            val configFile = findConfigFile()
            configFile.shouldNotBeNull()
            configFile.path shouldEndWith ".json"
            configFile.contents shouldStartWith "{"
        }
    }
})

class TestRenderer : RenderString {
    override fun invoke(event: LogEvent): String = event.message
}

object DoNothingSender : SendString {
    override fun invoke(eventString: String) {}
}
