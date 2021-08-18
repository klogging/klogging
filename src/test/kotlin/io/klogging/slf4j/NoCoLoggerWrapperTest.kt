package io.klogging.slf4j

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.slf4j.LoggerFactory
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
                LoggerFactory.getLogger(randomString()).info("User {id} called {name} is {age}", id, name, age)
                waitForDispatch()

                saved shouldHaveSize 1
                with(saved.first()) {
                    template shouldBe "User {id} called {name} is {age}"
                    items shouldContainExactly mapOf("id" to id, "name" to name, "age" to age)
                }
            }
        }

        it("passes `Throwable` object") {
            val saved = savedEvents()
            LoggerFactory.getLogger(randomString()).warn(randomString(), java.lang.RuntimeException(randomString()))
            waitForDispatch()

            saved shouldHaveSize 1
            saved.first().stackTrace shouldNotBe null
        }
    }
})
