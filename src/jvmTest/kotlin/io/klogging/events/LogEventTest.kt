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

package io.klogging.events

import io.klogging.context.logContext
import io.klogging.logger
import io.klogging.savedEvents
import io.klogging.waitForDispatch
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.launch

class LogEventTest : DescribeSpec({
    describe("Constructing logging events") {
        describe("with context items") {
            it("does not include context items if there are none") {
                val saved = savedEvents()
                val logger = logger("LogEventTest")
                logger.info("Test message")
                waitForDispatch()
                saved.first().items.size shouldBe 0
            }
            it("includes any items from the coroutine log context") {
                launch(logContext("colour" to "white")) {
                    val saved = savedEvents()
                    val logger = logger("LogEventTest")
                    logger.info("Test message")
                    waitForDispatch()
                    saved.first().items shouldContain ("colour" to "white")
                }
            }
        }
    }
})
