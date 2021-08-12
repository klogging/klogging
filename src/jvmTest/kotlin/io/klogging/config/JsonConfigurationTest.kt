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

import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.dispatching.STDOUT
import io.klogging.rendering.RENDER_SIMPLE
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

internal class JsonConfigurationTest : DescribeSpec({
    describe("Configuration from JSON") {
        describe("invalid JSON") {
            it("does not configure anything") {
                val config = configureFromJson("*** THIS IS NOT JSON ***")

                config?.apply {
                    sinks shouldHaveSize 0
                    configs shouldHaveSize 0
                }
            }
        }
        describe("simple, using built-in, named configuration") {
            it("sets up the configuration") {
                val simpleJsonConfig = """{ "configName": "DEFAULT_CONSOLE" }"""
                val config = configureFromJson(simpleJsonConfig)

                config?.apply {
                    sinks shouldHaveSize 1
                    sinks.keys.first() shouldBe "console"
                    with(sinks.values.first()) {
                        renderer shouldBe RENDER_SIMPLE
                        dispatcher shouldBe STDOUT
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
                          "dispatchTo": "STDOUT"
                        }
                      }
                    }
                """.trimIndent()
                val config = configureFromJson(jsonConfig)

                config?.apply { sinks shouldHaveSize 1 }
            }
        }
        describe("simple, using built-in, named renderers and dispatchers") {
            val simpleJsonConfig = """
                {
                  "sinks": {
                    "stdout": {
                      "renderWith": "RENDER_SIMPLE",
                      "dispatchTo": "STDOUT"
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
                val jsonConfig = readConfig(simpleJsonConfig)

                jsonConfig shouldNotBe null
                jsonConfig?.apply {
                    sinks shouldHaveSize 1
                    sinks.keys.first() shouldBe "stdout"
                    with(sinks.values.first()) {
                        renderWith shouldBe "RENDER_SIMPLE"
                        dispatchTo shouldBe "STDOUT"
                    }
                    logging shouldHaveSize 1
                    with(logging.first()) {
                        fromLoggerBase shouldBe "com.example"
                        levelRanges shouldHaveSize 1
                        with(levelRanges.first()) {
                            fromMinLevel shouldBe INFO
                            toSinks shouldHaveSize 1
                            toSinks.first() shouldBe "stdout"
                        }
                    }
                }
            }
            it("sets up built-in sinks") {
                val config = configureFromJson(simpleJsonConfig)

                config?.apply {
                    sinks shouldHaveSize 1
                    sinks.keys.first() shouldBe "stdout"
                    with(sinks.values.first()) {
                        renderer shouldBe RENDER_SIMPLE
                        dispatcher shouldBe STDOUT
                    }
                }
            }
            it("sets up the logging configurations") {
                val config = configureFromJson(simpleJsonConfig)

                config shouldNotBe null
                config?.apply {
                    configs shouldHaveSize 1
                    with(configs.first()) {
                        nameMatch.pattern shouldBe "^com.example.*"
                        ranges shouldHaveSize 1
                        with(ranges.first()) {
                            minLevel shouldBe INFO
                            maxLevel shouldBe FATAL
                            sinkNames shouldContainExactly listOf("stdout")
                        }
                    }
                }
            }
        }
        // describe("Klogging minimum log level") {
        //     it("is not changed if not set in JSON") {
        //         val config = configureFromJson("""{}""")
        //
        //         kloggingMinLogLevel shouldBe defaultKloggingMinLogLevel
        //     }
        //     it("is changed if set in JSON") {
        //         kloggingMinLogLevel = defaultKloggingMinLogLevel
        //         configureFromJson("""{"kloggingMinLogLevel":"DEBUG"}""")
        //
        //         kloggingMinLogLevel shouldBe DEBUG
        //     }
        // }
    }
})
