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

package io.klogging.context

import io.klogging.events.EventItems
import io.klogging.logger
import io.klogging.savedEvents
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContainExactly
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class OtherContextTest : DescribeSpec({
    afterTest { Context.clearContextItemExtractors() }
    describe("can extract event items from other coroutine context elements") {
        it("works as before if there are no other context elements") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()

            logger.info("Testing: {name}", "Fred")

            saved.first().items shouldContainExactly mapOf("name" to "Fred")
        }
        it("ignores context elements with no extractor configured") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()

            withContext(TestOtherContext()) {
                logger.info("Testing: {name}", "Fred")
            }

            saved.first().items shouldContainExactly mapOf("name" to "Fred")
        }
        it("gets event items from another context if an extractor is configured") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()
            Context.addContextItemExtractor(TestOtherContext, ::extractor)

            withContext(TestOtherContext("rhubarb")) {
                logger.info("Testing: {name}", "Fred")
            }

            saved.first().items shouldContainExactly mapOf(
                "other" to "rhubarb",
                "name" to "Fred",
            )
        }
        it("does not get event items from another context if there are none") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()
            Context.addContextItemExtractor(TestOtherContext, ::extractor)

            withContext(TestOtherContext()) {
                logger.info("Testing: {name}", "Fred")
            }

            saved.first().items shouldContainExactly mapOf("name" to "Fred")
        }
        it("combines event items from log context and other context") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()
            Context.addContextItemExtractor(TestOtherContext, ::extractor)

            withContext(TestOtherContext("apples") + logContext("this" to "that")) {
                logger.info("Testing: {name}", "Fred")
            }

            saved.first().items shouldContainExactly mapOf(
                "this" to "that",
                "other" to "apples",
                "name" to "Fred",
            )
        }
    }
})

class TestOtherContext(
    val value: String? = null,
) : AbstractCoroutineContextElement(TestOtherContext) {
    companion object Key : CoroutineContext.Key<TestOtherContext>
}

suspend fun extractor(element: TestOtherContext): EventItems =
    element.value?.let { mapOf("other" to it) } ?: mapOf()
