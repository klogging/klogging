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

package io.klogging.internal

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.config.DEFAULT_CONSOLE
import io.klogging.config.loggingConfiguration
import io.klogging.context.Context
import io.klogging.genLevel
import io.klogging.genLoggerName
import io.klogging.logEvent
import io.klogging.logger
import io.klogging.randomString
import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.savedEvents
import io.klogging.sending.STDERR
import io.klogging.sending.STDOUT
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class DispatcherTest : DescribeSpec({
    describe("sinksFor() function") {
        describe("when no loggers are configured") {
            it("returns no sinks") {
                checkAll(100, genLoggerName, genLevel) { name, level ->
                    loggingConfiguration { }
                    Dispatcher.sinksFor(name, level) shouldHaveSize 0
                }
            }
        }
        describe("with default console configuration") {
            it("returns the sink for INFO") {
                checkAll(100, genLoggerName) { name ->
                    loggingConfiguration { DEFAULT_CONSOLE() }
                    Dispatcher.sinksFor(name, INFO) shouldHaveSize 1
                }
            }
            it("returns no sinks for DEBUG") {
                checkAll(100, genLoggerName) { name ->
                    loggingConfiguration { DEFAULT_CONSOLE() }
                    Dispatcher.sinksFor(name, DEBUG) shouldHaveSize 0
                }
            }
        }
        describe("with base logger name configuration") {
            beforeTest {
                loggingConfiguration {
                    sink("console", RENDER_SIMPLE, STDOUT)
                    logging {
                        fromLoggerBase("com.example.Thing")
                        fromMinLevel(DEBUG) { toSink("console") }
                    }
                }
            }
            it("returns no sinks when the logger name is different to the configuration name") {
                checkAll(100, genLoggerName) { name ->
                    Dispatcher.sinksFor(name, DEBUG) shouldHaveSize 0
                }
            }
            it("returns the sink when the logger name matches the configuration name exactly") {
                Dispatcher.sinksFor("com.example.Thing", INFO) shouldHaveSize 1
            }
            it("returns the sink when the logger name starts with the configuration name") {
                Dispatcher.sinksFor("com.example.Thing\$Subclass", WARN) shouldHaveSize 1
            }
        }
        describe("with exact logger name configuration") {
            beforeTest {
                loggingConfiguration {
                    sink("console", RENDER_SIMPLE, STDOUT)
                    logging {
                        exactLogger("com.example.OtherThing")
                        fromMinLevel(DEBUG) { toSink("console") }
                    }
                }
            }
            it("returns no sinks when the logger name is different to the configuration name") {
                checkAll(100, genLoggerName) { name ->
                    Dispatcher.sinksFor(name, DEBUG) shouldHaveSize 0
                }
            }
            it("returns the sink when the logger name matches the configuration name exactly") {
                Dispatcher.sinksFor("com.example.OtherThing", INFO) shouldHaveSize 1
            }
            it("returns no sinks when the logger name starts with the configuration name") {
                Dispatcher.sinksFor("com.example.OtherThing\$Subclass", WARN) shouldHaveSize 0
            }
        }
        describe("with regex logger name configuration") {
            beforeTest {
                loggingConfiguration {
                    sink("console", RENDER_ANSI, STDOUT)
                    logging {
                        matchLogger("Level-[0-5]")
                        fromMinLevel(DEBUG) { toSink("console") }
                    }
                }
            }
            it("returns no sinks when the logger name doesn’t match") {
                Dispatcher.sinksFor("Level-6", WARN) shouldHaveSize 0
            }
            it("returns the sink when the logger name matches ") {
                Dispatcher.sinksFor("Level-3", INFO) shouldHaveSize 1
            }
        }
        describe("with minimum level specification") {
            beforeTest {
                loggingConfiguration {
                    sink("stdout", RENDER_SIMPLE, STDOUT)
                    sink("stderr", RENDER_SIMPLE, STDERR)
                    logging {
                        fromMinLevel(INFO) {
                            toSink("stdout")
                            toSink("stderr")
                        }
                    }
                }
            }
            it("returns no sinks when the level is below the configured level") {
                checkAll(100, genLoggerName) { name ->
                    Dispatcher.sinksFor(name, TRACE) shouldHaveSize 0
                }
            }
            it("returns the sinks when the level is at the configured level") {
                checkAll(100, genLoggerName) { name ->
                    Dispatcher.sinksFor(name, INFO) shouldHaveSize 2
                }
            }
            it("returns the sinks when the level is above the configured level") {
                checkAll(100, genLoggerName) { name ->
                    Dispatcher.sinksFor(name, ERROR) shouldHaveSize 2
                }
            }
        }
        describe("with stopOnMatch") {
            beforeTest {
                loggingConfiguration {
                    sink("rest", RENDER_SIMPLE, STDOUT)
                    sink("kord", RENDER_SIMPLE, STDOUT)
                    sink("svc", RENDER_SIMPLE, STDOUT)
                    logging {
                        fromLoggerBase("dev.kord.rest", stopOnMatch = true)
                        fromMinLevel(ERROR) { toSink("rest") }
                    }
                    logging {
                        fromLoggerBase("dev.kord")
                        fromMinLevel(DEBUG) { toSink("kord") }
                    }
                    logging {
                        fromLoggerBase("dev.kord.service")
                        fromMinLevel(INFO) { toSink("svc") }
                    }
                }
            }
            it("stops selecting sinks after matching a logger base with `stopOnMatch = true`") {
                Dispatcher.sinksFor("dev.kord.rest.RestClient", ERROR) shouldHaveSize 1
            }
            it("keeps selecting sinks after not matching a logger base with `stopOnMatch = true`") {
                Dispatcher.sinksFor("dev.kord.service.NikkyService", INFO) shouldHaveSize 2
            }
        }
        describe("!simple performance test") {
            beforeTest {
                repeat(10_000) { logger("dev.test.Logger-$it") }
                repeat(10_000) { logger("dev.test.sub.Logger-$it") }
                loggingConfiguration {
                    sink("stdout", RENDER_SIMPLE, STDOUT)
                    logging {
                        fromLoggerBase("dev.test")
                        fromMinLevel(INFO) { toSink("stdout") }
                    }
                    logging {
                        fromLoggerBase("org.apache")
                        fromMinLevel(WARN) { toSink("stdout") }
                    }
                }
            }
            it("`cachedSinksFor()` finds the sink for a logger out of 10,000 in less than 20 milliseconds") {
                repeat(100) {
                    val id = Random.nextLong(10_000)
                    repeat(100) {
                        eventually(
                            eventuallyConfig {
                                duration = 20.milliseconds
                                interval = 2.milliseconds
                            },
                        ) {
                            Dispatcher.cachedSinksFor("dev.test.Logger-$id", INFO)
                                .shouldHaveSize(1)
                                .first().name.shouldBe("stdout")
                        }
                    }
                }
            }
        }
    }
    describe("send() function") {
        it("adds base context items to the log event") {
            val events = savedEvents()
            val event = logEvent(level = INFO)
            Context.clearBaseContext()
            val key = randomString()
            val value = randomString()
            Context.addBaseContext(key to value)
            Dispatcher.send(event)
            eventually(1.seconds) {
                events.size shouldBe 1
                events.first().items[key] shouldBe value
            }
        }
        it("adds internal logger ID to the log event if Klogging min. log level is TRACE") {
            val events = savedEvents()
            loggingConfiguration(append = true) {
                kloggingMinLogLevel = TRACE
            }
            val event = logEvent(level = INFO)
            Dispatcher.send(event)
            eventually(1.seconds) {
                events.size shouldBe 1
                events.first().items["eventId"] shouldBe event.id
            }
        }
    }
    describe("sendDirect() function") {
        it("adds base context items to the log event") {
            val events = savedEvents(logDirect = false)
            val event = logEvent(level = INFO)
            Context.clearBaseContext()
            val key = randomString()
            val value = randomString()
            Context.addBaseContext(key to value)
            Dispatcher.sendDirect(event)
            events.size shouldBe 1
            events.first().items[key] shouldBe value
        }
        it("adds internal logger ID to the log event if Klogging min. log level is TRACE") {
            val events = savedEvents()
            loggingConfiguration(append = true) {
                kloggingMinLogLevel = TRACE
            }
            val event = logEvent(level = INFO)
            Dispatcher.sendDirect(event)
            events.first().items["eventId"] shouldBe event.id
        }
    }
})
