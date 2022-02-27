/*

   Copyright 2021-2022 Michael Strasser.

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
import io.klogging.events.LogEvent
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class SendSplunkTest : DescribeSpec({
    describe("`splunkEvent()` function") {
        val ts = Instant.fromEpochSeconds(1632804634, 266123000)
        val event = LogEvent(
            "id", ts, "local", "SendSplunkTest", "test-context", INFO,
            message = "This is a message", items = mapOf("colour" to "green")
        )
        val endpoint = SplunkEndpoint(
            "https://localhost:8088", "TOKEN", "logging-index", "logging-source", "false"
        )

        splunkEvent(endpoint, event) shouldBe """{
            |"time":1632804634.266123000,
            |"index":"logging-index",
            |"sourcetype":"logging-source",
            |"host":"local",
            |"event":{
            |"logger":"SendSplunkTest",
            |"level":"INFO",
            |"context":"test-context",
            |"message":"This is a message",
            |"colour":"green"
            |}}""".trimMargin().replace("\n", "")
    }
})
