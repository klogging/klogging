/*

   Copyright 2021-2022 Michael Strasser.

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

import io.klogging.Level
import io.klogging.eventSaver
import io.klogging.events.LogEvent
import io.klogging.logger
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import kotlin.time.Duration.Companion.seconds

class LoggingConfigTest : DescribeSpec({
    describe("`loggingConfiguration` DSL function") {
        it("matches `logging` blocks in order, stopping on match with `stopOnMatch = true`") {
            // Declare loggers first so DSL is not overwritten by JSON config on
            // classpath.
            val restLogger = logger("dev.kord.rest.RestClient")
            val svcLogger = logger("dev.kord.svc.BlahService")

            val testEvents = mutableListOf<LogEvent>()
            loggingConfiguration {
                sink("test", SinkConfiguration(eventSender = eventSaver(testEvents)))
                logging {
                    fromLoggerBase("dev.kord.rest", stopOnMatch = true)
                    fromMinLevel(Level.ERROR) { toSink("test") }
                }
                logging {
                    fromLoggerBase("dev.kord")
                    fromMinLevel(Level.DEBUG) { toSink("test") }
                }
            }

            restLogger.debug("Should not log at DEBUG")
            restLogger.info("Should not log at INFO")
            restLogger.warn("Should not log at WARN")
            restLogger.error("Should log at ERROR")

            svcLogger.debug("Should log at DEBUG")
            svcLogger.info("Should log at INFO")
            svcLogger.warn("Should log at WARN")
            svcLogger.error("Should log at ERROR")

            eventually(1.seconds) {
                testEvents.map { Pair(it.logger, it.level) }.shouldContainAll(
                    listOf(
                        Pair("dev.kord.rest.RestClient", Level.ERROR),
                        Pair("dev.kord.svc.BlahService", Level.DEBUG),
                        Pair("dev.kord.svc.BlahService", Level.INFO),
                        Pair("dev.kord.svc.BlahService", Level.WARN),
                        Pair("dev.kord.svc.BlahService", Level.ERROR)
                    )
                )
            }
        }
    }
})
