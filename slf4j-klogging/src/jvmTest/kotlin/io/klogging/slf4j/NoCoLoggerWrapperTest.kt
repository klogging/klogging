/*

   Copyright 2021-2023 Michael Strasser.

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

package io.klogging.slf4j

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import kotlin.random.Random

class NoCoLoggerWrapperTest : DescribeSpec({
    describe("NoCoLoggerWrapper") {

        describe("logs at level") {
            it("`TRACE`") {
                val saved = savedEvents()
                LoggerFactory.getLogger(randomString()).trace(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe TRACE
            }
            it("`DEBUG`") {
                val saved = savedEvents()
                LoggerFactory.getLogger(randomString()).trace(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe TRACE
            }
            it("`INFO`") {
                val saved = savedEvents()
                LoggerFactory.getLogger(randomString()).info(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe INFO
            }
            it("`WARN`") {
                val saved = savedEvents()
                LoggerFactory.getLogger(randomString()).warn(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe WARN
            }
            it("`ERROR`") {
                val saved = savedEvents()
                LoggerFactory.getLogger(randomString()).error(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe ERROR
            }
        }

        describe("uses minimum log level") {
            it("`DEBUG`") {
                val saved = savedEvents(minLevel = DEBUG)
                LoggerFactory.getLogger(randomString()).trace(randomString())
                LoggerFactory.getLogger(randomString()).debug(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe DEBUG
            }
            it("`INFO`") {
                val saved = savedEvents(minLevel = INFO)
                LoggerFactory.getLogger(randomString()).debug(randomString())
                LoggerFactory.getLogger(randomString()).info(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe INFO
            }
            it("`WARN`") {
                val saved = savedEvents(minLevel = WARN)
                LoggerFactory.getLogger(randomString()).info(randomString())
                LoggerFactory.getLogger(randomString()).warn(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe WARN
            }
            it("`ERROR`") {
                val saved = savedEvents(minLevel = ERROR)
                LoggerFactory.getLogger(randomString()).warn(randomString())
                LoggerFactory.getLogger(randomString()).error(randomString())
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().level shouldBe ERROR
            }
        }

        describe("uses message templates") {
            it("with one item") {
                val saved = savedEvents()
                val id = randomString()
                LoggerFactory.getLogger(randomString()).info("User {id} logged in", id)
                waitForDispatch()

                saved shouldHaveSize 1
                with(saved.first()) {
                    template shouldBe "User {id} logged in"
                    items shouldContainExactly mapOf("id" to id)
                }
            }
            it("with two items") {
                val saved = savedEvents()
                val id = randomString()
                val name = randomString()
                LoggerFactory.getLogger(randomString()).info("User {id} is called {name}", id, name)
                waitForDispatch()

                saved shouldHaveSize 1
                with(saved.first()) {
                    template shouldBe "User {id} is called {name}"
                    items shouldContainExactly mapOf("id" to id, "name" to name)
                }
            }
            it("with three items") {
                val saved = savedEvents()
                val id = randomString()
                val name = randomString()
                val age = Random.nextInt(50)
                LoggerFactory.getLogger(randomString()).info(
                    "User {id} called {name} is {age}",
                    id,
                    name,
                    age,
                )
                waitForDispatch()

                saved shouldHaveSize 1
                with(saved.first()) {
                    template shouldBe "User {id} called {name} is {age}"
                    items shouldContainExactly mapOf("id" to id, "name" to name, "age" to age)
                }
            }
        }

        describe("works with SLF4J placeholders") {
            it("replaces SLF4J placeholders with provided values") {
                val saved = savedEvents()
                val id = randomString()
                LoggerFactory.getLogger(randomString()).info("User {} logged in", id)
                waitForDispatch()

                saved shouldHaveSize 1
                with(saved.first()) {
                    template shouldBe "User $id logged in"
                    message shouldBe "User $id logged in"
                }
            }
            it("replaces SLF4J placeholders but does not add items") {
                val saved = savedEvents()
                val id = randomString()
                LoggerFactory.getLogger(randomString()).info("User {} logged in", id)
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().items shouldHaveSize 0
            }
            it("ignores SLF4J placeholders without provided values") {
                val saved = savedEvents()
                LoggerFactory.getLogger(randomString()).debug("User {} logged out")
                waitForDispatch()

                saved shouldHaveSize 1
                saved.first().message shouldBe "User {} logged out"
            }
            it("does not work with SLF4J placeholder and message template hole") {
                val saved = savedEvents()
                val id = randomString()
                val name = randomString()
                LoggerFactory.getLogger(randomString()).warn("User [{}] {name}", id, name)
                waitForDispatch()

                saved shouldHaveSize 1
                with(saved.first()) {
                    template shouldBe "User [$id] {name}"
                    message shouldBe "User [$id] {name}"
                    // First arg goes in items:
                    items shouldContainExactly mapOf("name" to id)
                }
            }
            it("does not work with message template hole and SLF4J placeholder") {
                val saved = savedEvents()
                val id = randomString()
                val name = randomString()
                LoggerFactory.getLogger(randomString()).warn("User {name} [{}]", name, id)
                waitForDispatch()

                saved shouldHaveSize 1
                with(saved.first()) {
                    template shouldBe "User {name} [$name]"
                    message shouldBe "User {name} [$name]"
                    // First arg goes in items:
                    items shouldContainExactly mapOf("name" to name)
                }
            }
        }

        it("passes `Throwable` object") {
            val saved = savedEvents()
            LoggerFactory.getLogger(randomString()).warn(
                randomString(),
                java.lang.RuntimeException(randomString()),
            )
            waitForDispatch()

            saved shouldHaveSize 1
            saved.first().stackTrace shouldNotBe null
        }

        it("includes MDC items when they are available") {
            val saved = savedEvents()
            val runId = randomString()
            MDC.putCloseable("runId", runId).use {
                LoggerFactory.getLogger(randomString()).info(randomString())
            }
            waitForDispatch()

            saved shouldHaveSize 1
            saved.first().items shouldContainExactly mapOf("runId" to runId)
        }
    }
})
