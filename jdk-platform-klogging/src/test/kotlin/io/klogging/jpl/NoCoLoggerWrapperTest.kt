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
    }
})
