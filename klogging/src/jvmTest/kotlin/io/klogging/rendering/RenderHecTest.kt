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
import io.klogging.events.LogEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class RenderHecTest : DescribeSpec({
    describe("`renderHec()` function") {
        val ts = Instant.fromEpochSeconds(1632804634, 266123000)
        val event = LogEvent(
            id = "id",
            timestamp = ts,
            host = "local",
            logger = "SendSplunkTest",
            context = "test-context",
            level = INFO,
            message = "This is a message",
            items = mapOf("colour" to "green"),
        )
        it("constructs a JSON event without optional values") {
            renderHec()(event) shouldBe """{
            |"time":1632804634.266123000,
            |"host":"local",
            |"event":{
            |"logger":"SendSplunkTest",
            |"level":"INFO",
            |"context":"test-context",
            |"message":"This is a message",
            |"colour":"green"
            |}}
            """.trimMargin().replace("\n", "")
        }
        it("constructs a JSON event with specified index") {
            renderHec(index = "general")(event) shouldBe """{
            |"time":1632804634.266123000,
            |"index":"general",
            |"host":"local",
            |"event":{
            |"logger":"SendSplunkTest",
            |"level":"INFO",
            |"context":"test-context",
            |"message":"This is a message",
            |"colour":"green"
            |}}
            """.trimMargin().replace("\n", "")
        }
        it("constructs a JSON event with a specified sourceType") {
            renderHec(sourceType = "Klogging")(event) shouldBe """{
            |"time":1632804634.266123000,
            |"sourcetype":"Klogging",
            |"host":"local",
            |"event":{
            |"logger":"SendSplunkTest",
            |"level":"INFO",
            |"context":"test-context",
            |"message":"This is a message",
            |"colour":"green"
            |}}
            """.trimMargin().replace("\n", "")
        }
        it("constructs a JSON event with a specified source") {
            renderHec(source = "Testing")(event) shouldBe """{
            |"time":1632804634.266123000,
            |"source":"Testing",
            |"host":"local",
            |"event":{
            |"logger":"SendSplunkTest",
            |"level":"INFO",
            |"context":"test-context",
            |"message":"This is a message",
            |"colour":"green"
            |}}
            """.trimMargin().replace("\n", "")
        }
    }
})
