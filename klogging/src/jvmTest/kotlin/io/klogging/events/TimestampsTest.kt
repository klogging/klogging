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

package io.klogging.events

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant

class TimestampsTest : DescribeSpec({
    describe("`Instant.decimalSeconds` extension property sets the correct values") {
        withData(
            listOf(
                "2023-08-02T10:08:42.0Z" to "1690970922.000000000",
                "2023-08-02T10:08:42.1Z" to "1690970922.100000000",
                "2023-08-02T10:08:42.12Z" to "1690970922.120000000",
                "2023-08-02T10:08:42.123Z" to "1690970922.123000000",
                "2023-08-02T10:08:42.1234Z" to "1690970922.123400000",
                "2023-08-02T10:08:42.12345Z" to "1690970922.123450000",
                "2023-08-02T10:08:42.123456Z" to "1690970922.123456000",
                "2023-08-02T10:08:42.1234567Z" to "1690970922.123456700",
                "2023-08-02T10:08:42.12345678Z" to "1690970922.123456780",
                "2023-08-02T10:08:42.123456789Z" to "1690970922.123456789",
            ),
        ) { (iso, decimal) ->
            Instant.parse(iso).decimalSeconds shouldBe decimal
        }
    }
})
