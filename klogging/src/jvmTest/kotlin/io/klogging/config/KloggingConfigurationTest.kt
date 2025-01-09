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
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.WARN
import io.klogging.fixturePath
import io.klogging.genLevel
import io.klogging.genLoggerName
import io.klogging.internal.KloggingEngine
import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.sending.STDERR
import io.klogging.sending.STDOUT
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll

internal class KloggingConfigurationTest : DescribeSpec({
    describe("Klogging configuration") {
        describe("default configuration") {
            it("has no sinks and no logging") {
                with(KloggingConfiguration()) {
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
                KloggingEngine.sinkConfigs() shouldContain ("console" to STDOUT_SIMPLE)
                KloggingEngine.sinkConfigs() shouldContain ("seq" to sinkConfig)
            }
            it("adds the default console") {
                loggingConfiguration { DEFAULT_CONSOLE() }

                with(KloggingEngine) {
                    sinkConfigs() shouldContain ("console" to STDOUT_SIMPLE)
                    configs() shouldHaveSize 1
                    with(configs().first()) {
                        nameMatcher shouldBe matchAll
                        ranges shouldHaveSize 1
                        with(ranges.first()) {
                            sinkNames shouldHaveSize 1
                            sinkNames.first() shouldBe "console"
                        }
                    }
                }
            }
            it("allows for complex logging configuration") {
                loggingConfiguration {
                    // Dispatch to standout output stream with simple message rendering.
                    sink("stdout", RENDER_SIMPLE, STDOUT)
                    // Dispatch to standout error stream with simple message rendering.
                    sink("stderr", RENDER_SIMPLE, STDERR)
                    // Dispatch to a Seq server with CLEF rendering by default.
                    sink("seq", seq("http://localhost:5341"))
                    logging {
                        // Log everything from `com.example` base.
                        fromLoggerBase("com.example")
                        // INFO level only.
                        atLevel(INFO) {
                            // To both standard out and Seq.
                            toSink("stdout")
                            toSink("seq")
                        }
                        // WARN level and above (more severe).
                        fromMinLevel(WARN) {
                            // To both standard error and Seq.
                            toSink("stderr")
                            toSink("seq")
                        }
                    }
                    logging {
                        // Exact logger name (e.g. one class).
                        exactLogger("com.example.service.FancyService")
                        // Log from DEBUG to Seq.
                        fromMinLevel(DEBUG) { toSink("seq") }
                    }
                    // Set minimum level of Klogging internal logging
                    kloggingMinLogLevel(DEBUG)
                    // Set minimum level for sending log events directly
                    minDirectLogLevel(INFO)
                }

                with(KloggingEngine) {
                    sinkConfigs() shouldHaveSize 3
                    sinkConfigs().keys shouldContainExactly setOf("stdout", "stderr", "seq")
                    sinkConfigs().values.map { it.stringSender } shouldContainAll setOf(
                        STDOUT,
                        STDERR,
                    )
                    sinkConfigs().values.map { it.renderer }.toSet() shouldContainExactly setOf(
                        RENDER_SIMPLE,
                        RENDER_CLEF,
                    )

                    configs() shouldHaveSize 2
                    with(configs().first()) {
                        ranges shouldHaveSize 2
                        ranges.first() shouldBe LevelRange(INFO, INFO)
                        with(ranges.first()) {
                            sinkNames shouldHaveSize 2
                            sinkNames.first() shouldBe "stdout"
                            sinkNames.last() shouldBe "seq"
                        }
                        ranges.last() shouldBe LevelRange(WARN, FATAL)
                        with(ranges.last()) {
                            sinkNames shouldHaveSize 2
                            sinkNames.first() shouldBe "stderr"
                            sinkNames.last() shouldBe "seq"
                        }
                    }
                    with(configs().last()) {
                        ranges shouldHaveSize 1
                        ranges.first() shouldBe LevelRange(DEBUG, FATAL)
                        with(ranges.first()) {
                            sinkNames shouldHaveSize 1
                            sinkNames.first() shouldBe "seq"
                        }
                    }
                }
            }
            it("can combine configurations") {
                loggingConfiguration { DEFAULT_CONSOLE() }
                loggingConfiguration(append = true) {
                    sink("stderr", RENDER_SIMPLE, STDERR)
                    logging {
                        exactLogger("Test")
                        atLevel(WARN) { toSink("stderr") }
                    }
                }

                with(KloggingEngine) {
                    sinks() shouldHaveSize 2
                    configs() shouldHaveSize 2
                }
            }
        }
        describe("minimumLevel() function") {
            it("returns INFO if there is no configuration") {
                KloggingEngine.minimumLevelOf(genLoggerName.next()) shouldBe INFO
            }
            it("returns INFO from the default console configuration") {
                loggingConfiguration { DEFAULT_CONSOLE() }
                KloggingEngine.minimumLevelOf(genLoggerName.next()) shouldBe INFO
            }
            it("returns the level of a single configuration that matches the logger name") {
                checkAll(genLoggerName, genLevel) { name, level ->
                    loggingConfiguration {
                        sink("stdout", RENDER_SIMPLE, STDOUT)
                        logging {
                            exactLogger(name)
                            atLevel(level) { toSink("stdout") }
                        }
                    }
                    KloggingEngine.minimumLevelOf(name) shouldBe level
                }
            }
            it("returns the minimum level of configurations that match the event name") {
                checkAll(genLoggerName) { name ->
                    loggingConfiguration {
                        sink("stdout", RENDER_SIMPLE, STDOUT)
                        logging { atLevel(WARN) { toSink("stdout") } }
                        logging { exactLogger(name); atLevel(INFO) { toSink("stdout") } }
                    }
                    KloggingEngine.minimumLevelOf(name) shouldBe INFO
                }
            }
            it("returns NONE if no configurations match the event name") {
                checkAll(genLoggerName) { name ->
                    loggingConfiguration {
                        sink("stdout", RENDER_SIMPLE, STDOUT)
                        logging { exactLogger(name); atLevel(INFO) { toSink("stdout") } }
                    }
                    KloggingEngine.minimumLevelOf("$name$name") shouldBe NONE
                }
            }
        }
        describe("append() function") {
            it("combines sinks") {
                val config = KloggingConfiguration().apply { sink("stdout", STDOUT_SIMPLE) }
                config.append(
                    KloggingConfiguration().apply { sink("stderr", seq("http://seq:5341")) },
                )

                with(config) {
                    sinks shouldHaveSize 2
                    sinks.keys shouldContainExactly setOf("stdout", "stderr")
                }
            }
            it("combines logging configurations") {
                val config = KloggingConfiguration().apply {
                    sink("stdout", STDOUT_SIMPLE)
                    logging { fromMinLevel(INFO) { toSink("stdout") } }
                }
                config.append(
                    KloggingConfiguration().apply {
                        logging { atLevel(DEBUG) { toSink("stdout") } }
                    },
                )

                with(config) {
                    configs shouldHaveSize 2
                }
            }
            it("selects the lower minimum level logging level") {
                val config = KloggingConfiguration().apply { kloggingMinLogLevel = INFO }

                config.append(KloggingConfiguration().apply { kloggingMinLogLevel = WARN })
                config.kloggingMinLogLevel shouldBe INFO

                config.append(KloggingConfiguration().apply { kloggingMinLogLevel = DEBUG })
                config.kloggingMinLogLevel shouldBe DEBUG
            }
        }
        describe("with `loggingConfigPath` value") {
            it("combines file and DSL configuration") {
                val configFilePath = fixturePath("klogging-test.json")
                loggingConfiguration {
                    loggingConfigPath(configFilePath)
                    sink("stdout", RENDER_ANSI, STDOUT)
                    logging {
                        fromLoggerBase("io.klogging.context.Context")
                        toMaxLevel(INFO) {
                            toSink("stdout")
                        }
                    }
                }

                with(KloggingEngine.sinks()) {
                    shouldHaveSize(2)
                    keys shouldContainExactly setOf("moon", "stdout")
                }
            }
            it("ignores if file is not found") {
                loggingConfiguration {
                    loggingConfigPath("/temp/missing-config.json")
                    sink("stdout", RENDER_ANSI, STDOUT)
                    logging {
                        fromLoggerBase("io.klogging.context.Context")
                        toMaxLevel(INFO) {
                            toSink("stdout")
                        }
                    }
                }

                KloggingEngine.sinks() shouldHaveSize 1
            }
        }
    }
})
