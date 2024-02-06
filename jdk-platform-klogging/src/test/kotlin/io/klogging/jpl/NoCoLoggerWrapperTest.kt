package io.klogging.jpl

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.util.ResourceBundle

val genName = Arb.string(10, 30)
val genMessage = Arb.string(10, 120)
val genException = genMessage.map { message -> Exception(message).fillInStackTrace() }
val getNumber = Arb.int(-50, 250)


class NoCoLoggerWrapperTest : DescribeSpec({
    describe("NoCoLoggerWrapper for JDK Platform Logging") {
        it("maps from JDK to Klogging levels") {
            System.Logger.Level.OFF.toKloggingLevel shouldBe NONE
            System.Logger.Level.ERROR.toKloggingLevel shouldBe ERROR
            System.Logger.Level.WARNING.toKloggingLevel shouldBe WARN
            System.Logger.Level.INFO.toKloggingLevel shouldBe INFO
            System.Logger.Level.DEBUG.toKloggingLevel shouldBe DEBUG
            System.Logger.Level.TRACE.toKloggingLevel shouldBe TRACE
            System.Logger.Level.ALL.toKloggingLevel shouldBe TRACE
        }
        describe("returns `isLoggable()` values as expected") {
            it("`System.Logger.Level.TRACE`") {
                val saved = savedEvents(minLevel = TRACE)
                System.getLogger(randomString()).log(System.Logger.Level.TRACE, randomString())

                saved shouldHaveSize 1
                saved.first().level shouldBe TRACE
            }
            it("`System.Logger.Level.DEBUG`") {
                val saved = savedEvents(minLevel = DEBUG)
                with(System.getLogger(randomString())) {
                    log(System.Logger.Level.TRACE, randomString())
                    log(System.Logger.Level.DEBUG, randomString())
                }

                saved shouldHaveSize 1
                saved.first().level shouldBe DEBUG
            }
            it("`System.Logger.Level.INFO`") {
                val saved = savedEvents(minLevel = INFO)
                with(System.getLogger(randomString())) {
                    log(System.Logger.Level.DEBUG, randomString())
                    log(System.Logger.Level.INFO, randomString())
                }

                saved shouldHaveSize 1
                saved.first().level shouldBe INFO
            }
            it("`System.Logger.Level.WARNING`") {
                val saved = savedEvents(minLevel = WARN)
                with(System.getLogger(randomString())) {
                    log(System.Logger.Level.INFO, randomString())
                    log(System.Logger.Level.WARNING, randomString())
                }

                saved shouldHaveSize 1
                saved.first().level shouldBe WARN
            }
            it("`System.Logger.Level.ERROR`") {
                val saved = savedEvents(minLevel = ERROR)
                with(System.getLogger(randomString())) {
                    log(System.Logger.Level.WARNING, randomString())
                    log(System.Logger.Level.ERROR, randomString())
                }

                saved shouldHaveSize 1
                saved.first().level shouldBe ERROR
            }
            it("`System.Logger.Level.ALL`") {
                val saved = savedEvents(minLevel = TRACE)
                with(System.getLogger(randomString())) {
                    log(System.Logger.Level.TRACE, randomString())
                    log(System.Logger.Level.DEBUG, randomString())
                    log(System.Logger.Level.INFO, randomString())
                    log(System.Logger.Level.WARNING, randomString())
                    log(System.Logger.Level.ERROR, randomString())
                }

                saved shouldHaveSize 5
            }
            it("`System.Logger.Level.OFF`") {
                val saved = savedEvents(minLevel = NONE)
                with(System.getLogger(randomString())) {
                    log(System.Logger.Level.TRACE, randomString())
                    log(System.Logger.Level.DEBUG, randomString())
                    log(System.Logger.Level.INFO, randomString())
                    log(System.Logger.Level.WARNING, randomString())
                    log(System.Logger.Level.ERROR, randomString())
                }

                saved.shouldBeEmpty()
            }
        }
        describe("logging messages") {
            it("sends only a string") {
                checkAll(genName, genMessage) { name, message ->
                    val saved = savedEvents()

                    System.getLogger(name).log(System.Logger.Level.INFO, message)

                    saved shouldHaveSize 1
                    saved.first().message shouldBe message
                }
            }
            it("sends a default message if supplied string is null") {
                checkAll(genName) { name ->
                    val saved = savedEvents()

                    System.getLogger(name).log(System.Logger.Level.INFO, null as String?)

                    saved shouldHaveSize 1
                    saved.first().message shouldBe "Null message supplied"
                }
            }
            it("sends a string and a throwable") {
                checkAll(genName, genMessage, genException) { name, message, exception ->
                    val saved = savedEvents()

                    System.getLogger(name).log(System.Logger.Level.INFO, message, exception)

                    saved shouldHaveSize 1
                    with(saved.first()) {
                        this.message shouldBe message
                        this.stackTrace shouldBe exception.stackTraceToString()
                    }
                }
            }
            it("sends the throwable message if supplied message is null") {
                checkAll(genName, genException) { name, exception ->
                    val saved = savedEvents()

                    System.getLogger(name).log(System.Logger.Level.INFO, null as String?, exception)

                    saved shouldHaveSize 1
                    with(saved.first()) {
                        this.message shouldBe exception.message
                        this.stackTrace shouldBe exception.stackTraceToString()
                    }
                }
            }
            it("sends a default message if supplied message and throwable are both null") {
                checkAll(genName) { name ->
                    val saved = savedEvents()

                    System.getLogger(name).log(System.Logger.Level.INFO, null as String?, null as Throwable?)

                    saved shouldHaveSize 1
                    with(saved.first()) {
                        this.message shouldBe "Null log message and throwable"
                    }
                }
            }
            it("sends a localised message if a resource bundle is supplied") {
                // src/test/resources/messages.properties contains:
                // key=Here is the log message
                val bundle = ResourceBundle.getBundle("messages")
                val saved = savedEvents()

                System.getLogger("TestLogger").log(System.Logger.Level.INFO, bundle, "key")

                saved shouldHaveSize 1
                saved.first().message shouldBe "Here is the log message"
            }
            it("sends a localised message if a resource bundle is supplied with an associated throwable") {
                // src/test/resources/messages.properties contains:
                // key=Here is the log message
                val bundle = ResourceBundle.getBundle("messages")
                val saved = savedEvents()

                System.getLogger("TestLogger").log(System.Logger.Level.INFO, bundle, "key", Exception())

                saved shouldHaveSize 1
                saved.first().message shouldBe "Here is the log message"
            }
            it("formats the message using `MessageTemplate` if parameters are supplied") {
                checkAll(genName, genMessage, getNumber) { name, message, number ->
                    val saved = savedEvents()

                    System.getLogger(name).log(System.Logger.Level.INFO, "Note: {0} had {1} things", message, number)

                    saved shouldHaveSize 1
                    saved.first().message shouldBe "Note: $message had $number things"
                }
            }
        }
    }
})
