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

import io.klogging.Level.INFO
import io.klogging.randomString
import io.klogging.savedEvents
import io.kotest.core.spec.style.DescribeSpec
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
                    contextItems = mapOf("runId" to runId),
                )

                saved.first().items shouldBe mapOf("runId" to runId)
            }
        }
        describe("e() function") {
            it("includes template items") {
                val value = randomString()
                val event = NoCoLoggerImpl("NoCoLoggerImplTest").e("Value is {value}", value)
                with(event) {
                    message shouldBe "Value is {value}"
                    template shouldBe "Value is {value}"
                    items shouldBe mapOf("value" to value)
                }
            }
            it("works without template items") {
                val event = NoCoLoggerImpl("NoCoLoggerImplTest").e("Message without template")
                with(event) {
                    message shouldBe "Message without template"
                    template shouldBe "Message without template"
                    items shouldBe mapOf()
                }
            }
        }
    }
})
