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

package io.klogging.impl

import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.WARN
import io.klogging.context.logContext
import io.klogging.events.timestampNow
import io.klogging.genException
import io.klogging.genLogEvent
import io.klogging.genLoggerName
import io.klogging.genMessage
import io.klogging.genString
import io.klogging.savedEvents
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import kotlinx.coroutines.withContext
import kotlin.random.Random

class KloggerImplTest : DescribeSpec({

    describe("KloggerImpl implementation of Klogger") {
        describe("logs any object") {
            it("logs a string in the message field") {
                checkAll(genLoggerName, genMessage) { name, message ->
                    val events = savedEvents()
                    KloggerImpl(name).warn(message)

                    events.first().message shouldBe message
                }
            }
            it("logs a LogEvent object with the specified level") {
                checkAll(genLoggerName, genLogEvent) { name, event ->
                    val events = savedEvents()
                    KloggerImpl(name).warn(event)

                    with(events.first()) {
                        timestamp shouldBe event.timestamp
                        host shouldBe event.host
                        logger shouldBe event.logger
                        level shouldBe WARN
                        template shouldBe event.template
                        message shouldBe event.message
                        stackTrace shouldBe event.stackTrace
                        items shouldBe event.items
                    }
                }
            }
            it("logs a LogEvent object with stack trace from a throwable") {
                checkAll(genLoggerName, genLogEvent, genException) { name, event, exception ->
                    val events = savedEvents()
                    KloggerImpl(name).error(exception, event)

                    with(events.first()) {
                        timestamp shouldBe event.timestamp
                        host shouldBe event.host
                        logger shouldBe event.logger
                        level shouldBe ERROR
                        template shouldBe event.template
                        message shouldBe event.message
                        stackTrace shouldBe exception.stackTraceToString()
                        items shouldBe event.items
                    }
                }
            }
            it("logs an exception with message and stack trace") {
                checkAll(genLoggerName, genException) { name, exception ->
                    val events = savedEvents()
                    KloggerImpl(name).warn(exception)

                    events.first().message shouldBe exception.message
                    events.first().stackTrace shouldNotBe null
                }
            }
            it("logs the string representation of anything else in the message field") {
                val events = savedEvents()
                val event = timestampNow()
                KloggerImpl("KloggerImplTest").info(event)

                events.first().message shouldBe event.toString()
            }
        }

        describe("optionally logs exception information") {
            it("does not include stack trace information if an exception is not provided") {
                val events = savedEvents()
                KloggerImpl("KloggerImplTest").warn { "Possible trouble" }

                events.first().stackTrace shouldBe null
            }
            it("includes stack trace information if an exception is provided as well as other information") {
                val events = savedEvents()
                KloggerImpl("KloggerImplTest").warn(RuntimeException("Oh noes!")) { "Big trouble!" }

                events.first().stackTrace shouldNotBe null
            }
        }

        describe("event construction function e()") {
            it("uses the template unchanged as message if there are no items") {
                checkAll(genLoggerName, genMessage) { name, tmpl ->
                    with(KloggerImpl(name).e(tmpl)) {
                        message shouldBe tmpl
                        template shouldBe tmpl
                    }
                }
            }
            it("uses message templating to complete the message") {
                val tmpl = "Hello {User}!"
                checkAll(genLoggerName, genString) { name, item ->
                    with(KloggerImpl(name).e(tmpl, item)) {
                        message shouldBe tmpl
                        template shouldBe tmpl
                        items shouldContain ("User" to item)
                    }
                }
            }
            it("extracts values into items") {
                val name = genString.next()
                val age = Random.nextInt(50)
                with(KloggerImpl("KloggerImplTest").e("{name} is {age} years old", name, age)) {
                    items shouldContainExactly mapOf("name" to name, "age" to age)
                }
            }
        }

        describe("emitEvent() function") {
            it("combines context items with event items") {
                checkAll(50, genLoggerName, genString, Arb.uuid()) { name, id, runId ->
                    val events = savedEvents()
                    val event = KloggerImpl(name).e("User {id} logged in", id)
                    withContext(logContext("run" to runId)) {
                        KloggerImpl(name).emitEvent(INFO, null, event)
                    }

                    events.first().items shouldContainAll mapOf(
                        "run" to runId,
                        "id" to id,
                    )
                }
            }
        }
    }
})
