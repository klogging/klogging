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

package io.klogging.dispatching

import io.klogging.config.KloggingConfiguration
import io.klogging.config.defaultConsole
import io.klogging.config.loggingConfiguration
import io.klogging.events.Level
import io.klogging.randomLevel
import io.klogging.randomString
import io.klogging.render.RENDER_SIMPLE
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class DispatcherTest : DescribeSpec({

    beforeTest {
        KloggingConfiguration.reset()
    }

    describe("sinksFor() function") {
        describe("when no loggers are configured") {
            it("returns no sinks") {
                Dispatcher.sinksFor(randomString(), randomLevel()) shouldHaveSize 0
            }
        }
        describe("with default console configuration") {
            it("returns the sink for INFO") {
                loggingConfiguration { defaultConsole() }
                val sinks = Dispatcher.sinksFor(randomString(), Level.INFO)

                sinks shouldHaveSize 1
                sinks.first().dispatcher shouldBe STDOUT
                sinks.first().renderer shouldBe RENDER_SIMPLE
            }
            it("returns no sinks for DEBUG") {
                loggingConfiguration { defaultConsole() }
                Dispatcher.sinksFor(randomString(), Level.DEBUG) shouldHaveSize 0
            }
        }
        describe("with base logger name configuration") {
            beforeTest {
                loggingConfiguration {
                    sink("console", RENDER_SIMPLE, STDOUT)
                    logging {
                        fromLoggerBase("com.example.Thing")
                        fromMinLevel(Level.DEBUG) { toSink("console") }
                    }
                }
            }
            it("returns no sinks when the logger name is different to the configuration name") {
                Dispatcher.sinksFor(randomString(), Level.DEBUG) shouldHaveSize 0
            }
            it("returns the sink when the logger name matches the configuration name exactly") {
                Dispatcher.sinksFor("com.example.Thing", Level.INFO) shouldHaveSize 1
            }
            it("returns the sink when the logger name starts with the configuration name") {
                Dispatcher.sinksFor("com.example.Thing\$Subclass", Level.WARN) shouldHaveSize 1
            }
        }
        describe("with exact logger name configuration") {
            beforeTest {
                loggingConfiguration {
                    sink("console", RENDER_SIMPLE, STDOUT)
                    logging {
                        exactLogger("com.example.OtherThing")
                        fromMinLevel(Level.DEBUG) { toSink("console") }
                    }
                }
            }
            it("returns no sinks when the logger name is different to the configuration name") {
                Dispatcher.sinksFor(randomString(), Level.DEBUG) shouldHaveSize 0
            }
            it("returns the sink when the logger name matches the configuration name exactly") {
                Dispatcher.sinksFor("com.example.OtherThing", Level.INFO) shouldHaveSize 1
            }
            it("returns the sink when the logger name starts with the configuration name") {
                Dispatcher.sinksFor("com.example.OtherThing\$Subclass", Level.WARN) shouldHaveSize 0
            }
        }
        describe("with minimum level specification") {
            beforeTest {
                loggingConfiguration {
                    sink("stdout", RENDER_SIMPLE, STDOUT)
                    sink("stderr", RENDER_SIMPLE, STDERR)
                    logging {
                        fromMinLevel(Level.INFO) {
                            toSink("stdout")
                            toSink("stderr")
                        }
                    }
                }
            }
            it("returns no sinks when the level is below the configured level") {
                Dispatcher.sinksFor(randomString(), Level.TRACE) shouldHaveSize 0
            }
            it("returns the sinks when the level is at the configured level") {
                Dispatcher.sinksFor(randomString(), Level.INFO) shouldHaveSize 2
            }
            it("returns the sinks when the level is above the configured level") {
                Dispatcher.sinksFor(randomString(), Level.ERROR) shouldHaveSize 2
            }
        }
    }
})
