package ktlogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import ktlogging.events.Level
import ktlogging.events.LogEvent
import ktlogging.events.hostname
import ktlogging.events.now
import ktlogging.template.templateItems
import java.time.Instant

class TestLogger(
    private val minLevel: Level = Level.TRACE
) : KtLogger {

    override val name: String = "TestLogger"

    internal var except: Exception? = null
    internal var logged: Any? = null

    override fun minLevel() = minLevel
    override suspend fun logMessage(level: Level, exception: Exception?, event: Any?) {
        except = exception
        logged = event
    }

    override suspend fun e(template: String, vararg values: Any?): LogEvent =
        LogEvent(randomString(), now(), hostname(), "TestLogger", Level.NONE, template, template, null,
            templateItems(template, *values).mapValues { e -> e.value.toString() })
}

class TestException(message: String) : Exception(message)

class KtLoggerTest : DescribeSpec({

    describe("KtLogger") {
        describe("for different logging styles") {
            it("logs a string message") {
                val message = randomString()
                with(TestLogger()) {
                    log(Level.INFO, message)
                    logged shouldBe message
                }
            }
            it("logs a string message with an exception") {
                val message = randomString()
                val exception = TestException(randomString())
                with(TestLogger()) {
                    log(Level.WARN, exception, message)
                    except shouldBe exception
                    logged shouldBe message
                }
            }
            it("logs a string message in a lambda") {
                val message = randomString()
                with(TestLogger()) {
                    info { message }
                    logged shouldBe message
                }
            }
            it("logs a string message in a lambda with an exception") {
                val message = randomString()
                val exception = TestException(randomString())
                with(TestLogger()) {
                    warn(exception) { message }
                    logged shouldBe message
                    except shouldBe exception
                }
            }
            it("logs an object") {
                val thing = Instant.now()
                with(TestLogger()) {
                    log(Level.DEBUG, thing)
                    logged shouldBe thing
                }
            }
            it("logs an object with an exception") {
                val thing = listOf(randomString(), randomString())
                val exception = TestException(randomString())
                with(TestLogger()) {
                    log(Level.WARN, exception, thing)
                    except shouldBe exception
                    logged shouldBe thing
                }
            }
            it("logs an object in a lambda") {
                val thing = randomString() to now()
                with(TestLogger()) {
                    info { thing }
                    logged shouldBe thing
                }
            }
            it("logs an object in a lambda with an exception") {
                val thing = setOf(Instant.now().minusSeconds(5), Instant.now())
                val exception = TestException(randomString())
                with(TestLogger()) {
                    error(exception) { thing }
                    except shouldBe exception
                    logged shouldBe thing
                }
            }
            it("logs a templated event using `e()` function in a lambda") {
                val id = randomString()
                with(TestLogger()) {
                    info { e("Id is {Id}", id) }
                    (logged as LogEvent).let {
                        it.message shouldBe "Id is {Id}"
                        it.template shouldBe "Id is {Id}"
                        it.items shouldContain ("Id" to id)
                    }
                }
            }
            it("logs a templated event using `e()` function in a lambda with an exception") {
                val id = randomString()
                val exception = TestException(randomString())
                with(TestLogger()) {
                    warn(exception) { e("Id is {Id}", id) }
                    except shouldBe exception
                    (logged as LogEvent).let {
                        it.message shouldBe "Id is {Id}"
                        it.template shouldBe "Id is {Id}"
                        it.items shouldContain ("Id" to id)
                    }
                }
            }
        }
    }

})
