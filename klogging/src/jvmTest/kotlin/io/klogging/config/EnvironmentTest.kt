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

package io.klogging.config

import io.klogging.genMessage
import io.klogging.genString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class EnvironmentTest : DescribeSpec({
    describe("`evalEnv()`: evaluate environment variables in strings") {
        it("returns a string as supplied without any env vars in it") {
            checkAll(genMessage) { string ->
                evalEnv(string) shouldBe string
            }
        }
        it("returns a string as supplied with unknown env vars in it") {
            checkAll(genString, genString) { str1, str2 ->
                val string = "$str1 \${UNKNOWN} $str2"
                evalEnv(string) shouldBe string
            }
        }
        it("evaluates a single env var in a string") {
            checkAll(genString, genString) { envName, envValue ->
                val string = "pre \${$envName} post"
                evalEnv(string, mapOf(envName to envValue)) shouldBe "pre $envValue post"
            }
        }
        it("evaluates multiple env vars in a string") {
            checkAll(genString, genString, genString) { name, value1, value2 ->
                // Ensure the two names are different: sometimes Kotest property tests
                // generate same values for names.
                val string = "start \${X$name} middle \${Y$name} end"

                evalEnv(
                    string,
                    mapOf(
                        "X$name" to value1,
                        "Y$name" to value2,
                    ),
                ) shouldBe "start $value1 middle $value2 end"
            }
        }
        it("evaluates an env var every time it occurs in a string") {
            checkAll(genString, genString) { name, value ->
                val string = "start \${$name} middle \${$name} end"

                evalEnv(string, mapOf(name to value)) shouldBe "start $value middle $value end"
            }
        }
    }
})
