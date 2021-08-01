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

package io.klogging

import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class LevelsTestLogger(private val level: Level) : Klogger {
    override val name = "LevelsTestLogger"

    override fun minLevel() = level

    var loggedMessage: Any? = null

    override suspend fun emitEvent(level: Level, exception: Exception?, event: Any?) {
        loggedMessage = event
    }

    override suspend fun e(template: String, vararg values: Any?): LogEvent {
        TODO("Not yet implemented")
    }
}

class LevelsTest : DescribeSpec({
    describe("at all logger levels") {
        it("`log()` calls `logMessage()` for all levels") {
            Level.values().forEach { loggerLevel ->
                val logger = LevelsTestLogger(loggerLevel)
                Level.values().forEach { eventLevel ->
                    randomString().let { msg ->
                        logger.log(eventLevel, msg)
                        logger.loggedMessage shouldBe msg
                    }
                }
            }
        }
    }
})
