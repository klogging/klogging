/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.klogging.templating.templateItems
import io.kotest.assertions.fail
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll

internal class KloggerTest : DescribeSpec({
    val thing = object {
        override fun toString() = genMessage.next()
    }

    describe("KLogger") {
        describe("does not log when level is NONE") {
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
                checkAll(genMessage) { message ->
                    with(TestLogger()) {
                        log(INFO, message)
                        logged shouldBe message
                    }
                }
            }
            it("logs a string message with an exception") {
                checkAll(genMessage, genException) { message, exception ->
                    with(TestLogger()) {
                        log(WARN, exception, message)
                        thrower shouldBe exception
                        logged shouldBe message
                    }
                }
            }
            it("logs a string message in a code block") {
                checkAll(genMessage) { message ->
                    with(TestLogger()) {
                        info { message }
                        logged shouldBe message
                    }
                }
            }
            it("logs a string message in a code block with an exception") {
                checkAll(genMessage, genException) { message, exception ->
                    with(TestLogger()) {
                        warn(exception) { message }
                        logged shouldBe message
                        thrower shouldBe exception
                    }
                }
            }
            it("logs an object") {
                with(TestLogger()) {
                    log(DEBUG, thing)
                    logged shouldBe thing
                }
            }
            it("logs an object with an exception") {
                checkAll(genMessage) { message ->
                    val exception = TestException(message)
                    with(TestLogger()) {
                        log(WARN, exception, thing)
                        thrower shouldBe exception
                        logged shouldBe thing
                    }
                }
            }
            it("logs an object in a code block") {
                with(TestLogger()) {
                    info { thing }
                    logged shouldBe thing
                }
            }
            it("logs an object in a lambda with an exception") {
                checkAll(genException) { exception ->
                    with(TestLogger()) {
                        error(exception) { thing }
                        thrower shouldBe exception
                        logged shouldBe thing
                    }
                }
            }
            it("logs a templated event") {
                val template = "Id is {Id}"
                checkAll(genString) { id ->
                    with(TestLogger()) {
                        debug(template, id)

                        (logged as LogEvent).let {
                            it.message shouldBe template
                            it.template shouldBe template
                            it.items shouldContain ("Id" to id)
                        }
                    }
                }
            }
            it("logs a templated event with an exception") {
                val template = "Id is {Id}"
                checkAll(genString, genException) { id, exception ->
                    with(TestLogger()) {
                        fatal(exception, template, id)

                        thrower shouldBe exception
                        (logged as LogEvent).let {
                            it.message shouldBe template
                            it.template shouldBe template
                            it.items shouldContain ("Id" to id)
                        }
                    }
                }
            }
            it("logs a templated event using `e()` function in a code block") {
                val template = "Id is {Id}"
                checkAll(genString) { id ->
                    with(TestLogger()) {
                        info { e(template, id) }
                        (logged as LogEvent).let {
                            it.message shouldBe template
                            it.template shouldBe template
                            it.items shouldContain ("Id" to id)
                        }
                    }
                }
            }
            it("logs a templated event using `e()` function in a code block with an exception") {
                val template = "Id is {Id}"
                checkAll(genString, genException) { id, exception ->
                    with(TestLogger()) {
                        warn(exception) { e(template, id) }
                        thrower shouldBe exception
                        (logged as LogEvent).let {
                            it.message shouldBe template
                            it.template shouldBe template
                            it.items shouldContain ("Id" to id)
                        }
                    }
                }
            }
        }

        describe("does not call a lambda argument if event level is below the logger minimum") {
            val levelTriangle = Level.values().flatMap { loggerLevel ->
                Level.values().filter { level -> level.ordinal < loggerLevel.ordinal }
                    .map { lowerLevel -> Pair(loggerLevel, lowerLevel) }
            }
            withData(levelTriangle) { (loggerLevel, eventLevel) ->
                TestLogger(loggerLevel).log(eventLevel) { fail("Should not be called") }
            }
        }
    }
})

private class TestLogger(private val minLevel: Level = TRACE) : Klogger {
    override val name: String = genLoggerName.next()
    override val loggerContextItems: EventItems = mapOf()

    var thrower: Throwable? = null
    var logged: Any? = null

    override fun minLevel() = minLevel
    override suspend fun emitEvent(level: Level, throwable: Throwable?, event: Any?, items: EventItems) {
        thrower = throwable
        logged = event
    }

    override suspend fun e(template: String, vararg values: Any?): LogEvent =
        LogEvent(
            logger = name,
            level = NONE,
            template = template,
            message = template,
            items = templateItems(template, *values).mapValues { e -> e.value.toString() },
        )
}
