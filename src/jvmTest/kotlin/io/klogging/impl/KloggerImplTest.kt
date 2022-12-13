/*

   Copyright 2021-2022 Michael Strasser.

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

package io.klogging.impl

import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.WARN
import io.klogging.context.logContext
import io.klogging.events.timestampNow
import io.klogging.logEvent
import io.klogging.randomString
import io.klogging.savedEvents
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class KloggerImplTest : DescribeSpec({

    describe("KloggerImpl implementation of Klogger") {
        describe("logs any object") {
            it("logs a string in the message field") {
                val events = savedEvents()
                val message = randomString()
                KloggerImpl("KloggerImplTest").warn(message)

                eventually(1.seconds) {
                    events.size shouldBe 1
                    events.first().message shouldBe message
                }
            }
            it("logs a LogEvent object with the specified level") {
                val events = savedEvents()
                val event = logEvent()
                KloggerImpl("KloggerImplTest").warn(event)

                eventually(1.seconds) {
                    events.size shouldBe 1
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
                val events = savedEvents()
                val event = logEvent()
                val exception = RuntimeException("Oh noes!")
                KloggerImpl("KloggerImplTest").error(exception, event)

                eventually(1.seconds) {
                    events.size shouldBe 1
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
                val events = savedEvents()
                val exception = RuntimeException("Some kind of problem")
                KloggerImpl("KloggerImplTest").warn(exception)

                eventually(1.seconds) {
                    events.size shouldBe 1
                    events.first().message shouldBe exception.message
                    events.first().stackTrace shouldNotBe null
                }
            }
            it("logs the string representation of anything else in the message field") {
                val events = savedEvents()
                val event = timestampNow()
                KloggerImpl("KloggerImplTest").info(event)

                eventually(1.seconds) {
                    events.size shouldBe 1
                    events.first().message shouldBe event.toString()
                }
            }
        }

        describe("optionally logs exception information") {
            it("does not include stack trace information if an exception is not provided") {
                val events = savedEvents()
                KloggerImpl("KloggerImplTest").warn { "Possible trouble" }

                eventually(1.seconds) {
                    events.size shouldBe 1
                    events.first().stackTrace shouldBe null
                }
            }
            it("includes stack trace information if an exception is provided as well as other information") {
                val events = savedEvents()
                KloggerImpl("KloggerImplTest").warn(RuntimeException("Oh noes!")) { "Big trouble!" }

                eventually(1.seconds) {
                    events.size shouldBe 1
                    events.first().stackTrace shouldNotBe null
                }
            }
        }

        describe("event construction function e()") {
            it("uses the template unchanged as message if there are no items") {
                val tmpl = randomString()
                with(KloggerImpl("KloggerImplTest").e(tmpl)) {
                    message shouldBe tmpl
                    template shouldBe tmpl
                }
            }
            it("uses message templating to complete the message") {
                val tmpl = "Hello {User}!"
                val item = randomString()
                with(KloggerImpl("KloggerImplTest").e(tmpl, item)) {
                    message shouldBe tmpl
                    template shouldBe tmpl
                    items shouldContain ("User" to item)
                }
            }
            it("extracts values into items") {
                val name = randomString()
                val age = Random.nextInt(50)
                with(KloggerImpl("KloggerImplTest").e("{name} is {age} years old", name, age)) {
                    items shouldContainExactly mapOf("name" to name, "age" to age)
                }
            }
        }

        describe("emitEvent() function") {
            it("combines context items with event items") {
                val events = savedEvents()
                val runId = randomString()
                val id = randomString()
                val event = KloggerImpl("KloggerImplTest").e("User {id} logged in", id)
                withContext(logContext("run" to runId)) {
                    KloggerImpl("KloggerImplTest").emitEvent(INFO, null, event)
                }

                eventually(1.seconds) {
                    events shouldHaveSize 1
                    events.first().items shouldContainAll mapOf(
                        "run" to runId,
                        "id" to id
                    )
                }
            }
        }
    }
})
