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

package io.klogging

import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.events.hostname
import io.klogging.template.templateItems
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import java.time.Instant

class TestLogger(
    private val minLevel: Level = Level.TRACE
) : Klogger {

    override val name: String = "TestLogger"

    internal var except: Exception? = null
    internal var logged: Any? = null

    override fun minLevel() = minLevel
    override suspend fun emitEvent(level: Level, exception: Exception?, event: Any?) {
        except = exception
        logged = event
    }

    override suspend fun e(template: String, vararg values: Any?): LogEvent =
        LogEvent(
            timestampNow(), hostname, "TestLogger", Thread.currentThread().name, Level.NONE, template, template, null,
            templateItems(template, *values).mapValues { e -> e.value.toString() }
        )
}

class TestException(message: String) : Exception(message)

class KloggerTest : DescribeSpec({

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
                val thing = randomString() to timestampNow()
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
            it("logs a templated event") {
                val template = "Id is {Id}"
                val id = randomString()
                with(TestLogger()) {
                    debug("Id is {Id}", id)

                    (logged as LogEvent).let {
                        it.message shouldBe template
                        it.template shouldBe template
                        it.items shouldContain ("Id" to id)
                    }
                }
            }
            it("logs a templated event with an exception") {
                val template = "Id is {Id}"
                val id = randomString()
                val exception = TestException(randomString())
                with(TestLogger()) {
                    fatal(exception, "Id is {Id}", id)

                    except shouldBe exception
                    (logged as LogEvent).let {
                        it.message shouldBe template
                        it.template shouldBe template
                        it.items shouldContain ("Id" to id)
                    }
                }
            }
            it("logs a templated event using `e()` function in a lambda") {
                val template = "Id is {Id}"
                val id = randomString()
                with(TestLogger()) {
                    info { e(template, id) }
                    (logged as LogEvent).let {
                        it.message shouldBe template
                        it.template shouldBe template
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
