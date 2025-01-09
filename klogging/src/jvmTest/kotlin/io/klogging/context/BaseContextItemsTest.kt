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
import io.klogging.savedEvents
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContainExactly
import kotlinx.coroutines.withContext

class BaseContextItemsTest : DescribeSpec({
    afterTest {
        Context.clearBaseContext()
    }
    describe("adds any base context items in log events") {
        it("behaves as before if there are no base context items") {
            val logger = logger<BaseContextItemsTest>()
            val saved = savedEvents()

            logger.info("Message from {name}", "Test")

            saved.first().items shouldContainExactly mapOf("name" to "Test")
        }
        it("includes any base context items") {
            val logger = logger<BaseContextItemsTest>()
            val saved = savedEvents()
            Context.addBaseContext("base" to "value")

            logger.info("Message from {name}", "Test")

            saved.first().items shouldContainExactly mapOf(
                "name" to "Test",
                "base" to "value",
            )
        }
        it("combines base context and log context items") {
            val logger = logger<BaseContextItemsTest>()
            val saved = savedEvents()
            Context.addBaseContext("base" to "base")

            withContext(logContext("log" to "log")) {
                logger.info("Message from {name}", "Test")
            }

            saved.first().items shouldContainExactly mapOf(
                "name" to "Test",
                "base" to "base",
                "log" to "log",
            )
        }
    }
})
