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

import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import mjs.kotest.description

val testLogger = object : BaseLogger {
    override val name: String = "A test logger"
    override val loggerContextItems: EventItems = mapOf()
}

internal class BaseLoggerTest : DescribeSpec({
    description("`BaseLogger` interface")
    describe("`messageAndStackTrace()` function used by `eventFrom()`") {
        it("uses the message and stack trace from a throwable as the object") {
            checkAll(genException) { exception ->
                messageAndStackTrace(obj = exception, throwable = null)
                    .shouldBe(exception.message to exception.stackTraceToString())
            }
        }
        it("""uses "Throwable" as the message if the specified throwable does not have one""") {
            messageAndStackTrace(obj = Exception(), throwable = null)
                .first shouldBe "Throwable"
        }
        it("uses `toString()` on an object as the event message") {
            checkAll(genMessage) { message ->
                val anObject = object {
                    override fun toString() = message
                }
                messageAndStackTrace(obj = anObject, throwable = null)
                    .first shouldBe message
            }
        }
        it("returns the object as string and no stack trace if no throwable specified") {
            checkAll(genMessage) { message ->
                messageAndStackTrace(obj = message, throwable = null)
                    .shouldBe(message to null)
            }
        }
        it("returns the object as string and stack trace if throwable specified") {
            checkAll(genMessage, genException) { message, exception ->
                messageAndStackTrace(obj = message, throwable = exception)
                    .shouldBe(message to exception.stackTraceToString())
            }
        }
    }
    describe("`eventFrom()` function that constructs a `LogEvent` from a range of types") {
        describe("copies an existing log event") {
            it("changing the level") {
                checkAll(genLevel, genLevel, genString, genMessage) { level, newLevel, logger, message ->
                    val event = LogEvent(level = level, logger = logger, message = message)
                    testLogger.eventFrom(level = newLevel, eventObject = event)
                        .level shouldBe newLevel
                }
            }
            it("using the stack trace from a specified throwable") {
                checkAll(genLevel, genString, genMessage, genException) { level, logger, message, exception ->
                    val event = LogEvent(level = level, logger = logger, message = message)
                    testLogger.eventFrom(level = level, throwable = exception, eventObject = event)
                        .stackTrace shouldBe exception.stackTraceToString()
                }
            }
            it("adding any context items") {
                checkAll(genLevel, genString, genMessage, genItem) { level, logger, message, item ->
                    val event = LogEvent(level = level, logger = logger, message = message, items = mapOf(item))
                    testLogger.eventFrom(level = level, eventObject = event)
                        .items.shouldContain(item)
                }
            }
        }
        it("uses the specified context") {
            checkAll(genLevel, genString) { level, context ->
                testLogger.eventFrom(context = context, level = level)
                    .context shouldBe context
            }
        }
        it("uses the specified level") {
            checkAll(genLevel, genString) { level, context ->
                testLogger.eventFrom(context = context, level = level)
                    .level shouldBe level
            }
        }
    }
})
