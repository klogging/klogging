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

package io.klogging.events

import io.klogging.Level.DEBUG
import io.klogging.Level.INFO
import io.klogging.context.logContext
import io.klogging.genLoggerName
import io.klogging.genMessage
import io.klogging.genString
import io.klogging.logger
import io.klogging.savedEvents
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import kotlinx.coroutines.launch

class LogEventTest : DescribeSpec({
    describe("Constructing logging events") {
        describe("with context items") {
            it("does not include context items if there are none") {
                val logger = logger("LogEventTest")
                val saved = savedEvents()
                logger.info("Test message")

                saved.first().items.size shouldBe 0
            }
            it("includes any items from the coroutine log context") {
                launch(logContext("colour" to "white")) {
                    val logger = logger("LogEventTest")
                    val saved = savedEvents()
                    logger.info("Test message")

                    saved.first().items shouldContain ("colour" to "white")
                }
            }
        }
    }

    describe("LogEvent.copyWith() extension function") {
        it("appends supplied items to those already in the event") {
            checkAll(genLoggerName, genString, Arb.uuid(), genMessage) { name, id, run, message ->
                val event = LogEvent(
                    logger = name,
                    level = INFO,
                    message = message,
                    items = mapOf("id" to id),
                )
                with(event.copyWith(DEBUG, null, mapOf("run" to run))) {
                    items shouldContainAll mapOf(
                        "id" to id,
                        "run" to run,
                    )
                }
            }
        }
        it("ignores any supplied items with the same keys as those already present") {
            checkAll(genLoggerName, genMessage, Arb.uuid(), Arb.uuid()) { name, msg, run1, run2 ->
                val event = LogEvent(
                    logger = name,
                    level = INFO,
                    message = msg,
                    items = mapOf("run" to run1),
                )
                with(event.copyWith(DEBUG, null, mapOf("run" to run2))) {
                    items shouldBe mapOf("run" to run1)
                }
            }
        }
    }
})
