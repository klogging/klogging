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

package io.klogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly

class LoggerContextTest : DescribeSpec({
    describe("Context items attached to loggers") {
        describe("defined at logger creation") {
            it("can be empty") {
                logger(randomString()).loggerContextItems.shouldBeEmpty()
                logger(LoggerContextTest::class).loggerContextItems.shouldBeEmpty()
                logger<LoggerContextTest>().loggerContextItems.shouldBeEmpty()
                noCoLogger(randomString()).loggerContextItems.shouldBeEmpty()
                noCoLogger(LoggerContextTest::class).loggerContextItems.shouldBeEmpty()
                noCoLogger<LoggerContextTest>().loggerContextItems.shouldBeEmpty()
            }
            it("can be set") {
                (randomString() to randomString()).let { context ->
                    logger(randomString(), context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                (randomString() to randomString()).let { context ->
                    logger(LoggerContextTest::class, context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                (randomString() to randomString()).let { context ->
                    logger<LoggerContextTest>(context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                (randomString() to randomString()).let { context ->
                    noCoLogger(randomString(), context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                (randomString() to randomString()).let { context ->
                    noCoLogger(
                        LoggerContextTest::class,
                        context
                    ).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                (randomString() to randomString()).let { context ->
                    noCoLogger<LoggerContextTest>(context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
            }
            it("updates any context items attached to a Klogger") {
                val loggerName = randomString()

                val oldContext = randomString() to randomString()
                val loggerOne = logger(loggerName, oldContext)
                loggerOne.loggerContextItems.shouldContainExactly(mapOf(oldContext))

                val newContext = randomString() to randomString()
                val loggerTwo = logger(loggerName, newContext)
                loggerTwo.loggerContextItems.shouldContainExactly(mapOf(newContext))
            }
            it("updates any context items attached to a NoCoLogger") {
                val loggerName = randomString()

                val oldContext = randomString() to randomString()
                val loggerOne = noCoLogger(loggerName, oldContext)
                loggerOne.loggerContextItems.shouldContainExactly(mapOf(oldContext))

                val newContext = randomString() to randomString()
                val loggerTwo = noCoLogger(loggerName, newContext)
                loggerTwo.loggerContextItems.shouldContainExactly(mapOf(newContext))
            }
        }
        describe("copied from another logger") {
            it("are set as its context items") {
                val contextItem = randomString() to randomString()
                logger(randomString(), logger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
                logger(LoggerContextTest::class, logger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
                logger<LoggerContextTest>(logger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
                noCoLogger(randomString(), noCoLogger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
                noCoLogger(LoggerContextTest::class, noCoLogger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
                noCoLogger<LoggerContextTest>(noCoLogger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
            }
            it("can be combined with items defined for this logger") {
                val contextItemOne = randomString() to randomString()
                val contextItemTwo = randomString() to randomString()
                logger(randomString(), logger(randomString(), contextItemOne), contextItemTwo)
                    .loggerContextItems.shouldContainExactly(mapOf(contextItemOne, contextItemTwo))
                logger(LoggerContextTest::class, logger(randomString(), contextItemOne), contextItemTwo)
                    .loggerContextItems.shouldContainExactly(mapOf(contextItemOne, contextItemTwo))
                logger<LoggerContextTest>(logger(randomString(), contextItemOne), contextItemTwo)
                    .loggerContextItems.shouldContainExactly(mapOf(contextItemOne, contextItemTwo))
                noCoLogger(randomString(), noCoLogger(randomString(), contextItemOne), contextItemTwo)
                    .loggerContextItems.shouldContainExactly(mapOf(contextItemOne, contextItemTwo))
                noCoLogger(LoggerContextTest::class, noCoLogger(randomString(), contextItemOne), contextItemTwo)
                    .loggerContextItems.shouldContainExactly(mapOf(contextItemOne, contextItemTwo))
                noCoLogger<LoggerContextTest>(noCoLogger(randomString(), contextItemOne), contextItemTwo)
                    .loggerContextItems.shouldContainExactly(mapOf(contextItemOne, contextItemTwo))
            }
            it("are copied between Klogger and NoCoLogger instances") {
                val contextItem = randomString() to randomString()
                logger(randomString(), noCoLogger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
                noCoLogger(randomString(), logger(randomString(), contextItem))
                    .loggerContextItems.shouldContainExactly(mapOf(contextItem))
            }
        }
    }
})
