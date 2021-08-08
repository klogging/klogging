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

@file:OptIn(ExperimentalTime::class)

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import io.klogging.events.hostname
import io.klogging.events.timestampNow
import io.klogging.template.templateItems
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.lang.Thread.currentThread
import kotlin.time.ExperimentalTime

class TestLogger(private val minLevel: Level = TRACE) : Klogger {
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
            randomString(),
            timestampNow(),
            hostname,
            "TestLogger",
            currentThread().name,
            NONE,
            template,
            template,
            null,
            templateItems(template, *values).mapValues { e -> e.value.toString() }
        )
}

class TestException(message: String) : Exception(message)

class KloggerTest : DescribeSpec({
    // Capture once rather than call several times: Avoid flaky tests if the OS sleeps our process
    val now = timestampNow()
    val thing = object {
        override fun toString() = "foo"
    }

    describe("KtLogger") {
        describe("does not log") {
            it("for a string message") {
                with(TestLogger()) {
                    log(NONE, "foo")
                    logged.shouldBeNull()
                }
            }
            it("for a string message with an exception") {
                with(TestLogger()) {
                    log(NONE, TestException("low bar"), "foo")
                    logged.shouldBeNull()
                }
            }
            it("for an object") {
                with(TestLogger()) {
                    log(NONE, thing)
                    logged.shouldBeNull()
                }
            }
            it("for an object with an exception") {
                with(TestLogger()) {
                    log(NONE, TestException("low bar"), thing)
                    logged.shouldBeNull()
                }
            }
        }

        describe("for different logging styles") {
            it("logs a string message") {
                val message = randomString()
                with(TestLogger()) {
                    log(INFO, message)
                    logged shouldBe message
                }
            }
            it("logs a string message with an exception") {
                val message = randomString()
                val exception = TestException(randomString())
                with(TestLogger()) {
                    log(WARN, exception, message)
                    except shouldBe exception
                    logged shouldBe message
                }
            }
            it("logs a string message in a code block") {
                val message = randomString()
                with(TestLogger()) {
                    info { message }
                    logged shouldBe message
                }
            }
            it("logs a string message in a code block with an exception") {
                val message = randomString()
                val exception = TestException(randomString())
                with(TestLogger()) {
                    warn(exception) { message }
                    logged shouldBe message
                    except shouldBe exception
                }
            }
            it("logs an object") {
                with(TestLogger()) {
                    log(DEBUG, thing)
                    logged shouldBe thing
                }
            }
            it("logs an object with an exception") {
                val exception = TestException(randomString())
                with(TestLogger()) {
                    log(WARN, exception, thing)
                    except shouldBe exception
                    logged shouldBe thing
                }
            }
            it("logs an object in a code block") {
                with(TestLogger()) {
                    info { thing }
                    logged shouldBe thing
                }
            }
            it("logs an object in a lambda with an exception") {
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
            it("logs a templated event using `e()` function in a code block") {
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
            it("logs a templated event using `e()` function in a code block with an exception") {
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
