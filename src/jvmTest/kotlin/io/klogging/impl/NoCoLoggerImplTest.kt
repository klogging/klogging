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

package io.klogging.impl

import io.klogging.Level.INFO
import io.klogging.randomString
import io.klogging.savedEvents
import io.klogging.waitForSend
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class NoCoLoggerImplTest : DescribeSpec({
    describe("NoCoLogger implementation") {
        describe("emitEvent() function") {
            it("includes context items") {
                val saved = savedEvents()
                val runId = randomString()
                NoCoLoggerImpl("NoCoLoggerImplTest").emitEvent(
                    level = INFO,
                    throwable = null,
                    event = "Message",
                    contextItems = mapOf("runId" to runId)
                )
                waitForSend()

                saved shouldHaveSize 1
                saved.first().items shouldBe mapOf("runId" to runId)
            }
        }
    }
})
