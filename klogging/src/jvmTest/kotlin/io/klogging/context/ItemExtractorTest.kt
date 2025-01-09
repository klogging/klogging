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

import io.klogging.logger
import io.klogging.noCoLogger
import io.klogging.savedEvents
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly

class ItemExtractorTest : DescribeSpec({
    afterTest { Context.clearItemExtractors() }
    describe("Item extractor function") {
        val logger = logger<ItemExtractorTest>()
        it("is not used if not set") {
            val saved = savedEvents()

            logger.info("No items here")

            saved.first().items.shouldBeEmpty()
        }
        it("adds to Klogger events with simple text messages") {
            val saved = savedEvents()
            Context.addItemExtractor { mapOf("one" to "two") }

            logger.info("An event")

            saved.first().items.shouldContainExactly(mapOf("one" to "two"))
        }
        it("adds to Klogger events with templated messages") {
            val saved = savedEvents()
            Context.addItemExtractor { mapOf("one" to "two") }

            logger.info("Hello, {name}", "Fred")

            saved.first().items.shouldContainExactly(mapOf("one" to "two", "name" to "Fred"))
        }
        it("can add Java thread-local values") {
            val saved = savedEvents()
            val threadLocal = JavaThreadLocalExtractor("value")
            Context.addItemExtractor(threadLocal.itemExtractor("item"))

            logger.info("Hello {name}", "John")

            saved.first().items.shouldContainExactly(mapOf("name" to "John", "item" to "value"))
            threadLocal.clear()
        }
        it("adds items to NoCoLogger events with simple text messages") {
            val ncLogger = noCoLogger<ItemExtractorTest>()
            val saved = savedEvents()
            Context.addItemExtractor { mapOf("coroutine?" to "nope") }

            ncLogger.info("This happened")

            saved.first().items.shouldContainExactly(mapOf("coroutine?" to "nope"))
        }
        it("adds items to NoCoLogger events with templated messages") {
            val ncLogger = noCoLogger<ItemExtractorTest>()
            val saved = savedEvents()
            Context.addItemExtractor { mapOf("coroutine?" to "nope") }

            ncLogger.info("This happened to {whom}", "Wallace")

            saved.first().items.shouldContainExactly(mapOf("coroutine?" to "nope", "whom" to "Wallace"))
        }
    }
})

private class JavaThreadLocalExtractor(
    private val value: String,
) {
    val threadLocal: ThreadLocal<String> = ThreadLocal.withInitial { value }

    fun itemExtractor(name: String): ItemExtractor = { mapOf(name to threadLocal.get()) }

    fun clear() {
        threadLocal.remove()
    }
}
