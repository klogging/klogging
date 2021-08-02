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

package io.klogging.config

import io.klogging.config.KloggingConfiguration.minimumLevelOf
import io.klogging.dispatching.STDERR
import io.klogging.dispatching.STDOUT
import io.klogging.events.Level
import io.klogging.randomLevel
import io.klogging.randomString
import io.klogging.render.RENDER_CLEF
import io.klogging.render.RENDER_SIMPLE
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

class KloggingConfigurationTest : DescribeSpec({

    beforeTest {
        KloggingConfiguration.reset()
    }

    describe("Klogging configuration") {
        describe("default configuration") {
            it("has no sinks and no logging") {
                with(KloggingConfiguration) {
                    sinks shouldHaveSize 0
                    configs shouldHaveSize 0
                }
            }
        }
        describe("configuration DSL") {
            it("adds sinks to the map") {
                val sinkConfig = seq("http://localhost:5341")
                loggingConfiguration {
                    sink("console", STDOUT_SIMPLE)
                    sink("seq", sinkConfig)
                }
                KloggingConfiguration.sinks shouldContain ("console" to STDOUT_SIMPLE)
                KloggingConfiguration.sinks shouldContain ("seq" to sinkConfig)
            }
            it("adds the default console") {
                loggingConfiguration { defaultConsole() }

                with(KloggingConfiguration) {
                    sinks shouldContain ("console" to STDOUT_SIMPLE)
                    configs shouldHaveSize 1
                    with(configs.first()) {
                        nameMatch.pattern shouldBe matchAllLoggers
                        ranges shouldHaveSize 1
                        with(ranges.first()) {
                            sinkNames shouldHaveSize 1
                            sinkNames.first() shouldBe "console"
                        }
                    }
                }
            }
            it("ignores a sink that has not been already defined") {
                loggingConfiguration {
                    logging {
                        sink("console", STDOUT_SIMPLE)
                        fromLoggerBase("com.example")
                        fromMinLevel(Level.INFO) {
                            toSink("console")
                            toSink("logstash")
                        }
                    }
                }

                with(KloggingConfiguration) {
                    configs shouldHaveSize 1
                    configs.first().ranges shouldHaveSize 1
                    configs.first().ranges.first().sinkNames shouldContain "console"
                }
            }
            it("allows for complex logging configuration") {
                loggingConfiguration {
                    // Dispatch to standout output stream with simple message rendering.
                    sink("stdout", STDOUT, RENDER_SIMPLE)
                    // Dispatch to standout error stream with simple message rendering.
                    sink("stderr", STDERR, RENDER_SIMPLE)
                    // Dispatch to a Seq server with CLEF rendering by default.
                    sink("seq", seq(server = "http://localhost:5341"))
                    logging {
                        // Log everything from `com.example` base.
                        fromLoggerBase("com.example")
                        // INFO level only.
                        atLevel(Level.INFO) {
                            // To both standard out and Seq.
                            toSink("stdout")
                            toSink("seq")
                        }
                        // WARN level and above (more severe).
                        fromMinLevel(Level.WARN) {
                            // To both standard error and Seq.
                            toSink("stderr")
                            toSink("seq")
                        }
                    }
                    logging {
                        // Exact logger name (e.g. one class).
                        exactLogger("com.example.service.FancyService")
                        // Log from DEBUG to Seq.
                        fromMinLevel(Level.DEBUG) { toSink("seq") }
                    }
                }

                with(KloggingConfiguration) {
                    sinks shouldHaveSize 3
                    sinks.keys shouldContainExactly setOf("stdout", "stderr", "seq")
                    sinks.values.map { it.dispatcher } shouldContainAll setOf(STDOUT, STDERR)
                    sinks.values.map { it.renderer }.toSet() shouldContainExactly setOf(RENDER_SIMPLE, RENDER_CLEF)

                    configs shouldHaveSize 2
                    with(configs.first()) {
                        nameMatch shouldBe Regex("^com.example.*")
                        ranges shouldHaveSize 2
                        ranges.first() shouldBe LevelRange(Level.INFO, Level.INFO)
                        with(ranges.first()) {
                            sinkNames shouldHaveSize 2
                            sinkNames.first() shouldBe "stdout"
                            sinkNames.last() shouldBe "seq"
                        }
                        ranges.last() shouldBe LevelRange(Level.WARN, Level.FATAL)
                        with(ranges.last()) {
                            sinkNames shouldHaveSize 2
                            sinkNames.first() shouldBe "stderr"
                            sinkNames.last() shouldBe "seq"
                        }
                    }
                    with(configs.last()) {
                        nameMatch shouldBe Regex("^com.example.service.FancyService\$")
                        ranges shouldHaveSize 1
                        ranges.first() shouldBe LevelRange(Level.DEBUG, Level.FATAL)
                        with(ranges.first()) {
                            sinkNames shouldHaveSize 1
                            sinkNames.first() shouldBe "seq"
                        }
                    }
                }
            }
            it("can combine configurations") {
                loggingConfiguration { defaultConsole() }
                loggingConfiguration(append = true) {
                    sink("stderr", STDERR, RENDER_SIMPLE)
                    logging {
                        exactLogger("Test")
                        atLevel(Level.WARN) { toSink("stderr") }
                    }
                }

                with(KloggingConfiguration) {
                    sinks shouldHaveSize 2
                    configs shouldHaveSize 2
                }
            }
        }
        describe("minimumLevel() function") {
            beforeTest { KloggingConfiguration.reset() }

            it("returns NONE if there is no configuration") {
                minimumLevelOf(randomString()) shouldBe Level.NONE
            }
            it("returns INFO from the default console configuration") {
                loggingConfiguration { defaultConsole() }
                minimumLevelOf(randomString()) shouldBe Level.INFO
            }
            it("returns the level of a single configuration that matches the logger name") {
                val name = randomString()
                val level = randomLevel()
                loggingConfiguration {
                    sink("stdout", STDOUT, RENDER_SIMPLE)
                    logging {
                        exactLogger(name)
                        atLevel(level) { toSink("stdout") }
                    }
                }

                minimumLevelOf(name) shouldBe level
            }
            it("returns the minimum level of configurations that match the event name") {
                val name = randomString()
                loggingConfiguration {
                    sink("stdout", STDOUT, RENDER_SIMPLE)
                    logging { atLevel(Level.WARN) { toSink("stdout") } }
                    logging { exactLogger(name); atLevel(Level.INFO) { toSink("stdout") } }
                }

                minimumLevelOf(name) shouldBe Level.INFO
            }
            it("returns NONE if no configurations match the event name") {
                val name = randomString()
                loggingConfiguration {
                    sink("stdout", STDOUT, RENDER_SIMPLE)
                    logging { exactLogger(name); atLevel(Level.INFO) { toSink("stdout") } }
                }

                minimumLevelOf(randomString()) shouldBe Level.NONE
            }
        }
    }
})
