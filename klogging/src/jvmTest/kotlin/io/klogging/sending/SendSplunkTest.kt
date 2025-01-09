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

package io.klogging.sending

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.util.UUID

class SendSplunkTest : DescribeSpec({
    describe("`SplunkEndpoint`") {
        it("`toString()` masks `hecToken` values with ******") {
            val token = UUID.randomUUID().toString()
            val endpoint = SplunkEndpoint("https://localhost:8088", token)
            with(endpoint.toString()) {
                shouldNotContain(token)
                shouldContain("********")
            }
        }
    }
})
