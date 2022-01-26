/*

   Copyright 2022 Michael Strasser.

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

package io.klogging.internal

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.config.DEFAULT_CONSOLE
import io.klogging.config.loggingConfiguration
import io.klogging.randomLevel
import io.klogging.randomString
import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.sending.STDERR
import io.klogging.sending.STDOUT
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize

internal class DispatcherTest : DescribeSpec({
    describe("sinksFor() function") {
        describe("when no loggers are configured") {
            it("returns no sinks") {
                loggingConfiguration { }
                Dispatcher.sinksFor(randomString(), randomLevel()) shouldHaveSize 0
            }
        }
        describe("with default console configuration") {
            it("returns the sink for INFO") {
                loggingConfiguration { DEFAULT_CONSOLE() }
                Dispatcher.sinksFor(randomString(), INFO) shouldHaveSize 1
            }
            it("returns no sinks for DEBUG") {
                loggingConfiguration { DEFAULT_CONSOLE() }
                Dispatcher.sinksFor(randomString(), DEBUG) shouldHaveSize 0
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
                Dispatcher.sinksFor(randomString(), DEBUG) shouldHaveSize 0
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
                Dispatcher.sinksFor(randomString(), DEBUG) shouldHaveSize 0
            }
            it("returns the sink when the logger name matches the configuration name exactly") {
                Dispatcher.sinksFor("com.example.OtherThing", INFO) shouldHaveSize 1
            }
            it("returns the sink when the logger name starts with the configuration name") {
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
                Dispatcher.sinksFor(randomString(), TRACE) shouldHaveSize 0
            }
            it("returns the sinks when the level is at the configured level") {
                Dispatcher.sinksFor(randomString(), INFO) shouldHaveSize 2
            }
            it("returns the sinks when the level is above the configured level") {
                Dispatcher.sinksFor(randomString(), ERROR) shouldHaveSize 2
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
    }
})
