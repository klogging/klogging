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
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.eventSaver
import io.klogging.events.LogEvent
import io.klogging.logger
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

class LoggingConfigTest : DescribeSpec({
    describe("`loggingConfiguration` DSL function") {
        it("matches `logging` blocks in order, stopping on match with `stopOnMatch = true`") {
            // Declare loggers first so DSL is not overwritten by JSON config on
            // classpath.
            val restLogger = logger("dev.kord.rest.RestClient")
            val svcLogger = logger("dev.kord.svc.BlahService")

            val testEvents = mutableListOf<LogEvent>()
            loggingConfiguration {
                minDirectLogLevel(TRACE)
                sink("test", SinkConfiguration(eventSender = eventSaver(testEvents)))
                logging {
                    fromLoggerBase("dev.kord.rest", stopOnMatch = true)
                    fromMinLevel(ERROR) { toSink("test") }
                }
                logging {
                    fromLoggerBase("dev.kord")
                    fromMinLevel(DEBUG) { toSink("test") }
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

            testEvents.map { Pair(it.logger, it.level) }.shouldContainAll(
                listOf(
                    Pair("dev.kord.rest.RestClient", ERROR),
                    Pair("dev.kord.svc.BlahService", DEBUG),
                    Pair("dev.kord.svc.BlahService", INFO),
                    Pair("dev.kord.svc.BlahService", WARN),
                    Pair("dev.kord.svc.BlahService", ERROR),
                ),
            )
        }
    }
    describe("`logging` DSL function") {
        fun testConfig(sinkName: String, levelsConfig: LoggingConfig.() -> Unit): MutableList<LogEvent> {
            val events = mutableListOf<LogEvent>()
            loggingConfiguration {
                minDirectLogLevel(TRACE)
                sink(sinkName, SinkConfiguration(eventSender = eventSaver(events)))
                logging { levelsConfig() }
            }
            return events
        }

        val logger = logger("test")
        it("`fromMinLevel` function specifies minimum inclusive level") {
            val testEvents = testConfig("test") {
                fromMinLevel(INFO) { toSink("test") }
            }
            logger.debug("Should not log at DEBUG")
            logger.info("Should log at INFO")
            logger.warn("Should log at WARN")

            testEvents.size shouldBe 2
            testEvents.map { it.message }.shouldContainAll("Should log at INFO", "Should log at WARN")
        }
        it("`toMaxLevel` function specifies minimum inclusive level") {
            val testEvents = testConfig("test") {
                toMaxLevel(INFO) { toSink("test") }
            }
            logger.debug("Should log at DEBUG")
            logger.info("Should log at INFO")
            logger.warn("Should not log at WARN")

            testEvents.size shouldBe 2
            testEvents.map { it.message }.shouldContainAll("Should log at DEBUG", "Should log at INFO")
        }
        it("`atLevel` function specifies exact logging level") {
            val testEvents = testConfig("test") {
                atLevel(WARN) { toSink("test") }
            }
            logger.info("Should not log at INFO")
            logger.warn("Should log at WARN")
            logger.error("Should not log at ERROR")

            testEvents.size shouldBe 1
            testEvents.map { it.message }.shouldContainAll("Should log at WARN")
        }
        it("`inLevelRange` function specifies a closed range of logging levels") {
            val testEvents = testConfig("test") {
                inLevelRange(DEBUG, INFO) { toSink("test") }
            }
            logger.trace("Should not log at TRACE")
            logger.debug("Should log at DEBUG")
            logger.info("Should log at INFO")
            logger.warn("Should not log at WARN")

            testEvents.size shouldBe 2
            testEvents.map { it.message }.shouldContainAll("Should log at DEBUG", "Should log at INFO")
        }
    }
})
