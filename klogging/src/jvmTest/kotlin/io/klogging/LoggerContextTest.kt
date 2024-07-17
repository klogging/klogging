/*

   Copyright 2021-2024 Michael Strasser.

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
                clearKloggers()
                logger(randomString()).loggerContextItems.shouldBeEmpty()
                clearKloggers()
                logger(LoggerContextTest::class).loggerContextItems.shouldBeEmpty()
                clearKloggers()
                logger<LoggerContextTest>().loggerContextItems.shouldBeEmpty()
                clearNoCoLoggers()
                noCoLogger(randomString()).loggerContextItems.shouldBeEmpty()
                clearNoCoLoggers()
                noCoLogger(LoggerContextTest::class).loggerContextItems.shouldBeEmpty()
                clearNoCoLoggers()
                noCoLogger<LoggerContextTest>().loggerContextItems.shouldBeEmpty()
            }
            it("can be set") {
                clearKloggers()
                (randomString() to randomString()).let { context ->
                    logger(randomString(), context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                clearKloggers()
                (randomString() to randomString()).let { context ->
                    logger(LoggerContextTest::class, context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                clearKloggers()
                (randomString() to randomString()).let { context ->
                    logger<LoggerContextTest>(context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                clearNoCoLoggers()
                (randomString() to randomString()).let { context ->
                    noCoLogger(randomString(), context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                clearNoCoLoggers()
                (randomString() to randomString()).let { context ->
                    noCoLogger(
                        LoggerContextTest::class,
                        context
                    ).loggerContextItems.shouldContainExactly(mapOf(context))
                }
                clearNoCoLoggers()
                (randomString() to randomString()).let { context ->
                    noCoLogger<LoggerContextTest>(context).loggerContextItems.shouldContainExactly(mapOf(context))
                }
            }
        }
    }
})
