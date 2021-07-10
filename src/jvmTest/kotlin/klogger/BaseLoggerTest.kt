package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.time.Instant

class BaseLoggerTest : DescribeSpec({
    describe("BaseLogger implementation of Klogger") {
        describe("logs any event object") {
            it("logs a string in the message field") {
                val events = savedEvents()
                val message = randomString()
                BaseLogger("BaseLoggerTest").warn(message)
                waitForDispatch()

                events.size shouldBe 1
                events.first().message shouldBe message
            }
            it("logs a LogEvent object as-is") {
                val events = savedEvents()
                val event = logEvent()
                BaseLogger("BaseLoggerTest").info(event)
                waitForDispatch()

                events.size shouldBe 1
                events.first() shouldBeSameInstanceAs event
            }
            it("logs the string representation of anything else in the message field") {
                val events = savedEvents()
                val event = Instant.now()
                logger("BaseLoggerTest").info(event)
                waitForDispatch()

                events.size shouldBe 1
                events.first().message shouldBe event.toString()
            }
        }

        describe("optionally logs exception information") {
            it("does not include stack trace information if an exception is not provided") {
                val events = savedEvents()
                logger("BaseLoggerTest").warn { "Possible trouble" }
                waitForDispatch()

                events.size shouldBe 1
                events.first().stackTrace shouldBe null
            }
            it("includes stack trace information if an exception is provided") {
                val events = savedEvents()
                logger("BaseLoggerTest").warn(RuntimeException("Oh noes!")) { "Big trouble!" }
                waitForDispatch()

                events.size shouldBe 1
                events.first().stackTrace shouldNotBe null
            }
        }
    }
})
