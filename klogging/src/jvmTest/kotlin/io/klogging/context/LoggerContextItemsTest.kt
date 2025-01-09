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
import io.klogging.randomString
import io.klogging.savedEvents
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldContain

class LoggerContextItemsTest : DescribeSpec({
    describe("Kloggers with logger context items") {
        it("include a single context item in logging output") {
            val events = savedEvents()
            val loggerContextItem = randomString() to randomString()
            logger(randomString(), loggerContextItem).info(randomString())
            events.first().items.shouldContain(loggerContextItem)
        }
        it("include multiple context items in logging output") {
            val events = savedEvents()
            val item1 = randomString() to randomString()
            val item2 = randomString() to randomString()
            logger(randomString(), item1, item2).info(randomString())
            events.first().items.shouldContain(item1)
            events.first().items.shouldContain(item2)
        }
    }
    describe("NoCoLoggers with logger context items") {
        it("include a single context item in logging output") {
            val events = savedEvents()
            val loggerContextItem = randomString() to randomString()
            noCoLogger(randomString(), loggerContextItem).info(randomString())
            events.first().items.shouldContain(loggerContextItem)
        }
        it("include multiple context items in logging output") {
            val events = savedEvents()
            val item1 = randomString() to randomString()
            val item2 = randomString() to randomString()
            noCoLogger(randomString(), item1, item2).info(randomString())
            events.first().items.shouldContain(item1)
            events.first().items.shouldContain(item2)
        }
    }
    describe("Loggers with context items from other loggers") {
        it("include those context items in logging output") {
            val events = savedEvents()
            val loggerContextItem = randomString() to randomString()
            logger(randomString(), noCoLogger(randomString(), loggerContextItem)).info(randomString())
            events.first().items.shouldContain(loggerContextItem)
        }
    }
})