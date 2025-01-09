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

import com.typesafe.config.ConfigFactory
import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.context.Context
import io.klogging.genString
import io.klogging.internal.KloggingEngine
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.sending.STDOUT
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.arbitrary.next
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon

@OptIn(ExperimentalSerializationApi::class)
internal class HoconConfigurationTest : DescribeSpec({
    describe("Configuration from HOCON") {
        describe("invalid HOCON") {
            it("does not configure anything") {
                // Suppress warning message from internal logger
                loggingConfiguration { kloggingMinLogLevel = Level.ERROR }

                // configure() catches exceptions and logs
                val config = HoconConfiguration.configure("*** THIS IS NOT HOCON ***")

                config?.apply {
                    sinks shouldHaveSize 0
                    configs shouldHaveSize 0
                }
            }
        }
        describe("simple, using built-in, named configuration") {
            it("sets up the configuration") {
                val simpleHoconConfig = """{ configName: DEFAULT_CONSOLE }"""
                val config = HoconConfiguration.configure(simpleHoconConfig)

                config?.apply {
                    sinks shouldHaveSize 1
                    sinks.keys.first() shouldBe "console"
                    with(sinks.values.first()) {
                        renderer shouldBe RENDER_SIMPLE
                        stringSender shouldBe STDOUT
                    }
                }
            }
            it("ignores any other configuration in the file") {
                val hoconConfig = """
                    {
                      configName: DEFAULT_CONSOLE,
                      sinks: {
                        stdout: {
                          renderWith: RENDER_SIMPLE,
                          sendTo: STDOUT
                        }
                      }
                    }
                """.trimIndent()
                val config = HoconConfiguration.configure(hoconConfig)

                config?.apply { sinks shouldHaveSize 1 }
            }
        }
        describe("setting base context items") {
            afterEach { Context.clearBaseContext() }
            it("adds any items to the base context") {
                val baseContextHoconConfig = """{baseContext:{app:testApp,buildNumber:"1.0.1"}}"""
                HoconConfiguration.configure(baseContextHoconConfig)

                KloggingEngine.baseContextItems.shouldContainExactly(
                    mapOf(
                        "app" to "testApp",
                        "buildNumber" to "1.0.1"
                    )
                )
            }
            it("evaluates environment variables in context item values") {
                withEnvironment("BUILD_NUMBER" to "2.0.22-ab8c14d") {
                    HoconConfiguration.configure("""{baseContext:{buildNumber:"${'$'}{BUILD_NUMBER}"}}""")

                    KloggingEngine.baseContextItems.shouldContainExactly(mapOf("buildNumber" to "2.0.22-ab8c14d"))
                }
            }
        }
        describe("simple, using built-in, named renderers and senders") {
            val simpleHoconConfig = """
                {
                  minDirectLogLevel: INFO,
                  sinks: {
                    stdout: {
                      renderWith: RENDER_SIMPLE,
                      sendTo: STDOUT
                    }
                  },
                  logging: [
                    {
                      fromLoggerBase: com.example,
                      levelRanges: [
                        {
                          fromMinLevel: INFO,
                          toSinks: [
                            stdout
                          ]
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()
            it("reads the configuration from string") {
                val hoconConfig = HoconConfiguration.readConfig(simpleHoconConfig)

                hoconConfig shouldNotBe null
                hoconConfig?.apply {
                    minDirectLogLevel shouldBe INFO
                    sinks shouldHaveSize 1
                    sinks.keys.first() shouldBe "stdout"
                    with(sinks.values.first()) {
                        renderWith shouldBe "RENDER_SIMPLE"
                        sendTo shouldBe "STDOUT"
                    }
                    logging shouldHaveSize 1
                    with(logging.first()) {
                        fromLoggerBase shouldBe "com.example"
                        levelRanges shouldHaveSize 1
                        with(levelRanges.first()) {
                            fromMinLevel shouldBe INFO
                            with(toSinks) {
                                shouldNotBeNull()
                                shouldHaveSize(1)
                                first() shouldBe "stdout"
                            }
                        }
                    }
                }
            }
            it("sets up built-in sinks") {
                val config = HoconConfiguration.configure(simpleHoconConfig)

                config?.apply {
                    sinks shouldHaveSize 1
                    sinks.keys.first() shouldBe "stdout"
                    with(sinks.values.first()) {
                        renderer shouldBe RENDER_SIMPLE
                        stringSender shouldBe STDOUT
                    }
                }
            }
            it("sets up the logging configurations") {
                val config = HoconConfiguration.configure(simpleHoconConfig)

                config shouldNotBe null
                config?.apply {
                    configs shouldHaveSize 1
                    with(configs.first()) {
                        ranges shouldHaveSize 1
                        with(ranges.first()) {
                            minLevel shouldBe INFO
                            maxLevel shouldBe FATAL
                            sinkNames shouldBe listOf("stdout")
                        }
                    }
                }
            }
        }
        describe("config with substitution from environment variable") {
            it("picks a value from environment variable") {
                withEnvironment("ENV_VAR_SUBSTITUTION_TEST", "RENDER_ANSI") {
                    HoconConfiguration.readConfig(
                        """
                            sinks {
                              stdout {
                                renderWith = RENDER_CLEF
                                renderWith = ${'$'}{ENV_VAR_SUBSTITUTION_TEST}
                                sendTo = STDOUT
                              }
                            }
                        """.trimIndent()
                    )
                }.shouldNotBeNull()
                    .sinks["stdout"].shouldNotBeNull()
                    .renderWith shouldBe "RENDER_ANSI"
            }
            it("ignores a non-existent environment variable") {
                HoconConfiguration.readConfig(
                    """
                        sinks {
                          stdout {
                            renderWith = RENDER_CLEF
                            renderWith = ${'$'}{?N0N_EXISTENT_ENV_VAR}
                            sendTo = STDOUT
                          }
                        }
                    """.trimIndent()
                )
                    .shouldNotBeNull()
                    .sinks["stdout"].shouldNotBeNull()
                    .renderWith shouldBe "RENDER_CLEF"
            }
        }
        describe("Klogging minimum log level") {
            beforeTest {
                KloggingEngine.setConfig(KloggingConfiguration())
            }
            it("is not changed if not set in HOCON") {
                HoconConfiguration.configure("""{}""")?.let { KloggingEngine.setConfig(it) }

                KloggingEngine.kloggingMinLogLevel() shouldBe defaultKloggingMinLogLevel
            }
            it("is changed if set in HOCON") {
                HoconConfiguration.configure("""{kloggingMinLogLevel:DEBUG}""")?.let {
                    KloggingEngine.setConfig(it)
                }

                KloggingEngine.kloggingMinLogLevel() shouldBe DEBUG
            }
        }
        describe("sink configuration") {
            fun parseSinkConfig(config: String) = Hocon.decodeFromConfig(
                FileSinkConfiguration.serializer(),
                ConfigFactory.parseString(config),
            ).toSinkConfiguration()
            describe("using `renderWith` and `sendTo` keys") {
                it("returns a configuration using names of built-in components") {
                    val sinkConfig = parseSinkConfig(
                        """{
                                renderWith: RENDER_SIMPLE,
                                sendTo: STDOUT
                            }
                        """.trimIndent(),
                    )

                    sinkConfig?.renderer shouldBe RENDER_SIMPLE
                    sinkConfig?.stringSender shouldBe STDOUT
                }
                it("returns null if `renderWith` key is missing") {
                    val sinkConfig = parseSinkConfig(
                        """{
                            sendTo: STDOUT
                        }
                        """.trimIndent(),
                    )

                    sinkConfig shouldBe null
                }
                it("returns null if `sendTo` key is missing") {
                    val sinkConfig = parseSinkConfig(
                        """{
                            renderWith: RENDER_SIMPLE
                        }
                        """.trimIndent(),
                    )

                    sinkConfig shouldBe null
                }
                it("returns null if names are not of built-in components") {
                    val sinkConfig = parseSinkConfig(
                        """{
                            renderWith: ${genString.next()},
                            sendTo: ${genString.next()}
                        }
                        """.trimIndent(),
                    )

                    sinkConfig shouldBe null
                }
            }
            describe("using `seqServer` key") {
                it("returns a Seq configuration with RENDER_CLEF if only that key is present") {
                    val sinkConfig = parseSinkConfig(
                        """{
                            seqServer: "http://localhost:5341"
                        }
                        """.trimIndent(),
                    )

                    sinkConfig?.renderer shouldBe RENDER_CLEF
                }
                it("returns a Seq configuration with another renderer if specified") {
                    val sinkConfig = parseSinkConfig(
                        """{
                            seqServer: "http://localhost:5341",
                            renderWith: RENDER_SIMPLE
                        }
                        """.trimIndent(),
                    )

                    sinkConfig?.renderer shouldBe RENDER_SIMPLE
                }
                it("returns a Seq configuration, overriding any other dispatcher") {
                    val sinkConfig = parseSinkConfig(
                        """{
                            seqServer: "http://localhost:5341",
                            sendTo: STDOUT
                        }
                        """.trimIndent(),
                    )

                    sinkConfig?.stringSender shouldNotBe STDOUT
                }
            }
        }
    }
})
