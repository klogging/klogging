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

package io.klogging

import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

internal class LevelsTest : DescribeSpec({
    describe("`log()` only emits events at the logger’s minimum level or above") {
        val allLevels = Level.values()
            .filter { it != Level.NONE }
            .flatMap { loggerLevel ->
                Level.values().map { eventLevel ->
                    LevelsCase(
                        loggerLevel,
                        eventLevel,
                        if (
                            loggerLevel == Level.NONE ||
                            loggerLevel.ordinal > eventLevel.ordinal
                        ) {
                            "no"
                        } else {
                            "YES"
                        },
                    )
                }
            }
        withData(allLevels) { (loggerLevel, eventLevel, message) ->
            val logger = LevelsTestLogger(loggerLevel)
            logger.log(eventLevel, message)

            if (logger.isLevelEnabled(eventLevel)) {
                logger.loggedMessage shouldBe "YES"
            } else {
                logger.loggedMessage.shouldBeNull()
            }
        }
    }
})

private data class LevelsCase(
    val loggerMin: Level,
    val event: Level,
    val emit: String,
)

private class LevelsTestLogger(private val level: Level) : Klogger {
    override val name = "LevelsTestLogger"
    override val loggerContextItems: EventItems = mapOf()

    override fun minLevel() = level

    var loggedMessage: Any? = null

    override suspend fun emitEvent(level: Level, throwable: Throwable?, event: Any?, items: EventItems) {
        loggedMessage = event
    }

    override suspend fun e(template: String, vararg values: Any?): LogEvent {
        TODO("Not yet implemented")
    }
}
