/*

   Copyright 2021-2024 Michael Strasser.

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

package io.klogging

import io.klogging.Level.FATAL
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import mjs.kotest.description

internal class BaseLoggerTest : DescribeSpec({
    description("Abstract base logger class")
    describe("`BaseLogger.eventFrom()` extension function that constructs a `LogEvent` from a range of types") {
        class TestLogger(
            override val name: String = "A test logger",
        ) : BaseLogger

        it("makes an event for an event object") {
            checkAll(genString, genMessage) { context, message ->
                val event = TestLogger().eventFrom(
                    context,
                    FATAL,
                    null,
                    object {
                        override fun toString() = message
                    },
                )

                event.message shouldBe message
            }
        }
    }
})
