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
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.context.Context
import io.klogging.events.LogEvent
import io.klogging.genMessage
import io.klogging.genString
import io.klogging.internal.KloggingEngine
import io.klogging.logEvent
import io.klogging.randomString
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.rendering.RenderString
import io.klogging.sending.STDOUT
import io.klogging.sending.SendString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll
import kotlinx.serialization.json.Json

internal class JsonConfigurationTest : DescribeSpec({
    describe("Configuration from JSON") {
        describe("invalid JSON") {
            it("does not configure anything") {
                // Suppress warning message from internal logger
                loggingConfiguration { kloggingMinLogLevel = ERROR }

                // configure() catches exceptions and logs
                val config = JsonConfiguration.configure("*** THIS IS NOT JSON ***")

                config?.apply {
                    sinks shouldHaveSize 0
                    configs shouldHaveSize 0
                }
            }
        }
        describe("setting base context items") {
            afterEach { Context.clearBaseContext() }
            it("adds any items to the base context") {
                val baseContextJsonConfig = """{"baseContext":{"app":"testApp","buildNumber":"1.0.1"}}"""
                JsonConfiguration.configure(baseContextJsonConfig)

                KloggingEngine.baseContextItems.shouldContainExactly(
                    mapOf(
                        "app" to "testApp",
                        "buildNumber" to "1.0.1"
                    )
                )
            }
            it("evaluates environment variables in context item values") {
                withEnvironment("BUILD_NUMBER" to "2.0.22-f78ca4d") {
                    JsonConfiguration.configure("""{baseContext:{buildNumber:"${'$'}{BUILD_NUMBER}"}}""")

                    KloggingEngine.baseContextItems.shouldContainExactly(mapOf("buildNumber" to "2.0.22-f78ca4d"))
                }
            }
        }
        describe("simple, using built-in, named configuration") {
            it("sets up the configuration") {
                val simpleJsonConfig = """{ "configName": "DEFAULT_CONSOLE" }"""
                val config = JsonConfiguration.configure(simpleJsonConfig)

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
                val jsonConfig = """
                    {
                      "configName":"DEFAULT_CONSOLE",
                      "sinks": {
                        "stdout": {
                          "renderWith": "RENDER_SIMPLE",
                          "sendTo": "STDOUT"
                        }
                      }
                    }
                """.trimIndent()
                val config = JsonConfiguration.configure(jsonConfig)

                config?.apply { sinks shouldHaveSize 1 }
            }
        }
        describe("simple, using built-in, named renderers and senders") {
            val simpleJsonConfig = """
                {
                  "minDirectLogLevel": "INFO",
                  "sinks": {
                    "stdout": {
                      "renderWith": "RENDER_SIMPLE",
                      "sendTo": "STDOUT"
                    }
                  },
                  "logging": [
                    {
                      "fromLoggerBase": "com.example",
                      "levelRanges": [
                        {
                          "fromMinLevel": "INFO",
                          "toSinks": [
                            "stdout"
                          ]
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()
            it("reads the configuration from string") {
                val jsonConfig = JsonConfiguration.readConfig(simpleJsonConfig)

                jsonConfig shouldNotBe null
                jsonConfig?.apply {
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
                val config = JsonConfiguration.configure(simpleJsonConfig)

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
                val config = JsonConfiguration.configure(simpleJsonConfig)

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
        describe("Klogging minimum log level") {
            beforeTest {
                KloggingEngine.setConfig(KloggingConfiguration())
            }
            it("is not changed if not set in JSON") {
                JsonConfiguration.configure("""{}""")?.let { KloggingEngine.setConfig(it) }

                KloggingEngine.kloggingMinLogLevel() shouldBe defaultKloggingMinLogLevel
            }
            it("is changed if set in JSON") {
                JsonConfiguration.configure("""{"kloggingMinLogLevel":"DEBUG"}""")?.let {
                    KloggingEngine.setConfig(it)
                }

                KloggingEngine.kloggingMinLogLevel() shouldBe DEBUG
            }
        }
        describe("`logging` level range") {
            it("reads `atLevel`") {
                Json.decodeFromString<FileLevelRange>(
                    """{"atLevel": "INFO"}""",
                ).toLevelRange() shouldBe LevelRange(INFO, INFO)
            }
            it("reads `fromMinLevel`") {
                Json.decodeFromString<FileLevelRange>(
                    """{"fromMinLevel": "INFO"}""",
                ).toLevelRange() shouldBe LevelRange(INFO, FATAL)
            }
            it("reads `toMaxLevel`") {
                Json.decodeFromString<FileLevelRange>(
                    """{"toMaxLevel": "INFO"}""",
                ).toLevelRange() shouldBe LevelRange(TRACE, INFO)
            }
            it("reads `fromMinLevel` and `toMaxLevel`") {
                Json.decodeFromString<FileLevelRange>(
                    """{"fromMinLevel": "DEBUG", "toMaxLevel": "INFO"}""",
                ).toLevelRange() shouldBe LevelRange(DEBUG, INFO)
            }
        }
        describe("sink configuration") {
            describe("using `renderWith` and `sendTo` keys") {
                it("returns a configuration using names of built-in components") {
                    val sinkConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                        "renderWith": "RENDER_SIMPLE",
                        "sendTo": "STDOUT"
                    }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    sinkConfig?.renderer shouldBe RENDER_SIMPLE
                    sinkConfig?.stringSender shouldBe STDOUT
                }
                it("returns null if `renderWith` key is missing") {
                    val sinkConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                        "sendTo": "STDOUT"
                    }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    sinkConfig shouldBe null
                }
                it("returns null if `sendTo` key is missing") {
                    val sinkConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                        "renderWith": "RENDER_SIMPLE"
                    }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    sinkConfig shouldBe null
                }
                it("returns null if names are not of built-in components") {
                    val sinkConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                        "renderWith": "${genString.next()}",
                        "sendTo": "${genString.next()}"
                    }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    sinkConfig shouldBe null
                }
                it("returns configuration with the name of a `RenderString` class on the classpath") {
                    val renderStringClassName = RenderMessageOnly::class.java.name
                    val renderStringConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                            "renderWith": "$renderStringClassName",
                            "sendTo": "STDOUT"
                            }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    renderStringConfig?.renderer.let { renderer ->
                        renderer.shouldNotBeNull()
                        checkAll(genMessage) { message ->
                            renderer(logEvent(message = message)) shouldBe message
                        }
                    }
                }
                it("returns configuration with the name of a `Sender` class on the classpath") {
                    val senderClassName = StringSavingSender::class.java.name
                    val senderConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                            "renderWith": "RENDER_SIMPLE",
                            "sendTo": "$senderClassName"
                        }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    senderConfig?.stringSender.let { sender ->
                        sender.shouldNotBeNull()
                        val eventString = randomString()
                        savedString = ""
                        sender(eventString)
                        savedString shouldBe eventString
                    }
                }
            }
            describe("using `seqServer` key") {
                it("returns a Seq configuration with RENDER_CLEF if only that key is present") {
                    val sinkConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                        "seqServer": "http://localhost:5341"
                    }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    sinkConfig?.renderer shouldBe RENDER_CLEF
                }
                it("returns a Seq configuration with another renderer if specified") {
                    val sinkConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                        "seqServer": "http://localhost:5341",
                        "renderWith": "RENDER_SIMPLE"
                    }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    sinkConfig?.renderer shouldBe RENDER_SIMPLE
                }
                it("returns a Seq configuration, overriding any other dispatcher") {
                    val sinkConfig = Json.decodeFromString<FileSinkConfiguration>(
                        """{
                        "seqServer": "http://localhost:5341",
                        "sendTo": "STDOUT"
                    }
                        """.trimIndent(),
                    ).toSinkConfiguration()

                    sinkConfig?.stringSender shouldNotBe STDOUT
                }
            }
        }
    }
})

class RenderMessageOnly : RenderString {
    override operator fun invoke(event: LogEvent): String = event.message
}

var savedString: String = ""

class StringSavingSender : SendString {
    override fun invoke(eventString: String) {
        savedString = eventString
    }
}
