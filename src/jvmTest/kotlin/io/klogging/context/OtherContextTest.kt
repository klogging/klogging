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

package io.klogging.context

import io.klogging.config.loggingConfiguration
import io.klogging.logger
import io.klogging.savedEvents
import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

class OtherContextTest : DescribeSpec({
    describe("can extract event items from other coroutine context elements") {
        it("works as before if there are no other context elements") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()

            logger.info("Testing: {name}", "Fred")

            eventually(1.seconds) {
                saved.first().items shouldContain ("name" to "Fred")
            }
        }
        it("ignores context elements with no extractor configured") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()

            withContext(TestOtherContext()) {
                logger.info("Testing: {name}", "Fred")
            }

            eventually(1.seconds) {
                saved.first().items shouldContain ("name" to "Fred")
            }
        }
        it("gets event items from another context if an extractor is configured") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()
            val extractor: ContextItemExtractor = { _ ->
                mapOf("Other" to "Value")
            }
            loggingConfiguration(append = true) {
                addContextItemExtractor(TestOtherContext, extractor)
            }

            withContext(TestOtherContext()) {
                logger.info("Testing: {name}", "Fred")
            }

            val event = eventually(1.seconds) {
                saved.first()
            }
            with(event.items) {
                shouldContain("Other" to "Value")
                shouldContain("name" to "Fred")
            }
        }
        it("does not get event items from another context if there are none") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()
            val extractor: ContextItemExtractor = { _ -> emptyMap() }
            loggingConfiguration(append = true) {
                addContextItemExtractor(TestOtherContext, extractor)
            }

            withContext(TestOtherContext()) {
                logger.info("Testing: {name}", "Fred")
            }

            eventually(1.seconds) {
                saved.first().items shouldContain ("name" to "Fred")
            }
        }
        it("combines event items from log context and other context") {
            val logger = logger<OtherContextTest>()
            val saved = savedEvents()
            val extractor: ContextItemExtractor = { _ ->
                mapOf("Other" to "Value")
            }
            loggingConfiguration(append = true) {
                addContextItemExtractor(TestOtherContext, extractor)
            }

            withContext(TestOtherContext() + logContext("this" to "that")) {
                logger.info("Testing: {name}", "Fred")
            }

            val event = eventually(1.seconds) {
                saved.first()
            }
            with(event.items) {
                size shouldBe 3
                shouldContain("this" to "that")
                shouldContain("Other" to "Value")
                shouldContain("name" to "Fred")
            }
        }
    }
})

class TestOtherContext : AbstractCoroutineContextElement(TestOtherContext) {
    companion object Key : CoroutineContext.Key<TestOtherContext>
}
