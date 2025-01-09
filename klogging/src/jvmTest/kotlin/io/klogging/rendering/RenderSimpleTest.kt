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

package io.klogging.rendering

import io.klogging.Level.INFO
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import io.klogging.events.timestampNow
import io.klogging.randomString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Suppress("ktlint:max-line-length")
class RenderSimpleTest : DescribeSpec({
    describe("Render a `LogEvent` to a simple string") {
        it("omits null stack trace") {
            val ts = timestampNow()
            val event = LogEvent(
                timestamp = ts,
                host = "test.local",
                logger = "Test",
                context = "test-thread",
                level = INFO,
                template = null,
                message = "Message",
                stackTrace = null,
                items = mapOf(),
            )

            RENDER_SIMPLE(event) shouldBe "${ts.localString} INFO [test-thread] Test : Message"
        }

        it("includes items only if they are present") {
            val ts = timestampNow()
            val event = LogEvent(
                timestamp = ts,
                host = "test.local",
                logger = "Test",
                context = "test-thread",
                level = WARN,
                template = null,
                message = "Message",
                stackTrace = null,
                items = mapOf("colour" to "green"),
            )

            RENDER_SIMPLE(event) shouldBe "${ts.localString} WARN [test-thread] Test : Message : {colour=green}"
        }

        describe("evaluates variables in a message") {

            it("substitutes all substrings matching item entry's \"{KEY}\" for VALUE") {
                val ts = timestampNow()
                val event = LogEvent(
                    timestamp = ts,
                    host = "test.local",
                    logger = "Test",
                    context = "test-thread",
                    level = INFO,
                    template = null,
                    message = "User {user} logged in and has a role {user_role}",
                    stackTrace = null,
                    items = mapOf("user" to "Samuel", "user_role" to "admin"),
                )

                RENDER_SIMPLE(event) shouldBe "${ts.localString} INFO [test-thread] Test : User Samuel logged in and has a role admin : {user=Samuel, user_role=admin}"
            }

            it("doesn't substitute with null") {
                val ts = timestampNow()
                val event = LogEvent(
                    timestamp = ts,
                    host = "test.local",
                    logger = "Test",
                    context = "test-thread",
                    level = INFO,
                    template = null,
                    message = "User {user} logged in and has a role {user_role}",
                    stackTrace = null,
                    items = mapOf("user" to "Samuel", "user_role" to null),
                )

                RENDER_SIMPLE(event) shouldBe "${ts.localString} INFO [test-thread] Test : User Samuel logged in and has a role {user_role} : {user=Samuel, user_role=null}"
            }
        }
        it("puts a stack trace starting on the next line") {
            val ts = timestampNow()
            val stackTrace = "${randomString()}\n${randomString()}\n${randomString()}"
            val event = LogEvent(
                timestamp = ts,
                host = "test.local",
                logger = "Test",
                context = "test-thread",
                level = INFO,
                template = null,
                message = "Message",
                stackTrace = stackTrace,
                items = mapOf(),
            )

            RENDER_SIMPLE(event) shouldBe "${ts.localString} INFO [test-thread] Test : Message\n$stackTrace"
        }
    }
})
