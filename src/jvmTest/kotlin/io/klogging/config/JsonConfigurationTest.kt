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

import io.klogging.Level
import io.klogging.dispatching.STDOUT
import io.klogging.rendering.RENDER_SIMPLE
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class JsonConfigurationTest : DescribeSpec({
    describe("Configuration from JSON") {
        describe("simple configuration using built-in renderers and dispatchers") {
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
                            fromMinLevel shouldBe Level.INFO
                            toSinks shouldHaveSize 1
                            toSinks.first() shouldBe "stdout"
                        }
                    }
                }
            }
            it("sets up built-in sinks") {
                configureFromJson(simpleJsonConfig)

                with(KloggingConfiguration) {
                    sinks shouldHaveSize 1
                    sinks.keys.first() shouldBe "stdout"
                    with(sinks.values.first()) {
                        renderer shouldBe RENDER_SIMPLE
                        dispatcher shouldBe STDOUT
                    }
                }
            }
            it("sets up the logging configurations") {
                configureFromJson(simpleJsonConfig)

                with(KloggingConfiguration) {
                    configs shouldHaveSize 1
                    with(configs.first()) {
                        nameMatch.pattern shouldBe "^com.example.*"
                        ranges shouldHaveSize 1
                        with(ranges.first()) {
                            minLevel shouldBe Level.INFO
                            maxLevel shouldBe Level.FATAL
                            sinkNames shouldContainExactly listOf("stdout")
                        }
                    }
                }
            }
        }
    }
})
