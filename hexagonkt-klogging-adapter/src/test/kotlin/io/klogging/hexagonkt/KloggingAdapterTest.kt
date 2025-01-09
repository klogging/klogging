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

package io.klogging.hexagonkt

import com.hexagonkt.core.logging.Logger
import com.hexagonkt.core.logging.LoggingLevel.DEBUG
import com.hexagonkt.core.logging.LoggingLevel.ERROR
import com.hexagonkt.core.logging.LoggingLevel.INFO
import com.hexagonkt.core.logging.LoggingLevel.OFF
import com.hexagonkt.core.logging.LoggingLevel.TRACE
import com.hexagonkt.core.logging.LoggingLevel.WARN
import com.hexagonkt.core.logging.LoggingManager
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class KloggingAdapterTest : DescribeSpec({
    beforeAny {
        LoggingManager.adapter = KloggingAdapter()
    }
    describe("Using `KloggingAdapter`") {
        it("sends string messages as Klogging events") {
            val logger = Logger(KloggingAdapterTest::class)
            checkAll<String> { message ->
                val saved = savedEvents()
                logger.info { message }
                saved.size shouldBe 1
                saved.first().message shouldBe message
            }
        }
        it("sends logs with exceptions as Klogging events") {
            val logger = Logger(KloggingAdapterTest::class)
            val saved = savedEvents()
            val errorMessage = randomString()
            val message = randomString()
            logger.warn(TestException(errorMessage)) { message }

            saved.size shouldBe 1
            with(saved.first()) {
                this.message shouldBe message
                this.stackTrace?.lines()?.first() shouldBe "io.klogging.hexagonkt.TestException: $errorMessage"
            }
        }
        describe("sends events from Hexagon logging levels") {
            val logger = Logger(KloggingAdapterTest::class)
            withData(TRACE, DEBUG, INFO, WARN, ERROR) { level ->
                val saved = savedEvents()
                logger.log(level) { randomString() }

                saved.size shouldBe 1
                saved.first().level shouldBe level.kloggingLevel
            }
        }
        it("ignores Hexagon logging level OFF") {
            val logger = Logger(KloggingAdapterTest::class)
            val saved = savedEvents()
            logger.log(OFF) { randomString() }

            saved.shouldBeEmpty()
        }
        it("allows Hexagon logger level to be changed") {
            val loggerName = randomString()
            val logger = Logger(loggerName)

            // savedEvents() sets min logger level to TRACE for all loggers
            savedEvents()
            logger.isTraceEnabled() shouldBe true
            logger.isDebugEnabled() shouldBe true

            LoggingManager.adapter.setLoggerLevel(loggerName, INFO)

            logger.isDebugEnabled() shouldBe false
            logger.isInfoEnabled() shouldBe true
        }
    }
})

class TestException(message: String) : Exception(message)
