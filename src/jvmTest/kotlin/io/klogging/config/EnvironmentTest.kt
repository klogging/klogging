/*

   Copyright 2022 Michael Strasser.

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

package io.klogging.config

import io.klogging.randomString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class EnvironmentTest : DescribeSpec({
    describe("`evalEnv()`: evaluate environment variables in strings") {
        it("returns a string as supplied without any env vars in it") {
            val string = randomString()
            evalEnv(string) shouldBe string
        }
        it("returns a string as supplied with unknown env vars in it") {
            val string = randomString() + "\${UNKNOWN}" + randomString()
            evalEnv(string) shouldBe string
        }
        it("evaluates a single env var in a string") {
            val envName = randomString()
            val envValue = randomString()
            val string = "pre \${$envName} post"

            evalEnv(string, mapOf(envName to envValue)) shouldBe "pre $envValue post"
        }
        it("evaluates multiple env vars in a string") {
            val names = listOf(randomString(), randomString())
            val values = listOf(randomString(), randomString())
            val string = "start \${${names[0]}} middle \${${names[1]}} end"

            evalEnv(
                string,
                mapOf(
                    names[0] to values[0],
                    names[1] to values[1],
                )
            ) shouldBe "start ${values[0]} middle ${values[1]} end"
        }
        it("evaluates an env var every time it occurs in a string") {
            val name = randomString()
            val value = randomString()
            val string = "start \${$name} middle \${$name} end"

            evalEnv(string, mapOf(name to value)) shouldBe "start $value middle $value end"
        }
    }
})
