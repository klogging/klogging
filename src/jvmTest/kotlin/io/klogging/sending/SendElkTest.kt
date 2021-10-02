/*

   Copyright 2021 Michael Strasser.

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

package io.klogging.sending

import io.klogging.Level.INFO
import io.klogging.logEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain
import kotlinx.datetime.Instant

class SendElkTest : DescribeSpec({
    describe("`ecsEvent()` function") {
        it("includes ECS Base field `@timestamp`") {
            val ts = Instant.parse("2021-09-29T10:08:42.123456Z")
            val event = logEvent(timestamp = ts)
            ecsEvent(event) shouldContain """"@timestamp":"2021-09-29T10:08:42.123456Z""""
        }
        it("includes ECS Base field `message` containing evaluated template") {
            val event = logEvent(
                message = "User {userId} logged in",
                items = mapOf("userId" to "Freddo Frog")
            )
            ecsEvent(event) shouldContain """"message":"User Freddo Frog logged in""""
        }
        it("includes ECS Error field `error.stack_trace` if a stack trace is present") {
            val event = logEvent(stackTrace = "Not really a stack trace")
            ecsEvent(event) shouldContain """"error.stack_trace":"Not really a stack trace""""
        }
        it("includes ECS Error field `error.message` if a stack trace is present") {
            val event = logEvent(stackTrace = "Pretend", message = "Oh noes! Something went wrong")
            ecsEvent(event) shouldContain """"error.message":"Oh noes! Something went wrong""""
        }
        it("includes ECS Host field `host.name`") {
            val event = logEvent(host = "local-test")
            ecsEvent(event) shouldContain """"host.name":"local-test""""
        }
        it("includes ECS Log field `log.logger`") {
            val event = logEvent(logger = "SendElkTest")
            ecsEvent(event) shouldContain """"log.logger":"SendElkTest""""
        }
        it("includes ECS Log field `log.level`") {
            val event = logEvent(level = INFO)
            ecsEvent(event) shouldContain """"log.level":"INFO""""
        }
        it("defines custom label `context` with value of `context`") {
            val event = logEvent(context = "test-context")
            ecsEvent(event).also {
                it shouldContain """"labels":{"context":"test-context"}"""
            }
        }
        it("defines nested object `items` with value of `items`") {
            val event = logEvent(items = mapOf("colour" to "chartreuse"))
            ecsEvent(event).also {
                it shouldContain """"items":{"colour":"chartreuse"}"""
            }
        }
    }
})
