/*

   Copyright 2021-2023 Michael Strasser.

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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe

internal class FilesTest : DescribeSpec({
    describe("Configuration files") {
        it("should find `klogging.json` on the classpath") {
            val jsonText = findFileConfigText()

            jsonText shouldNotBe null
        }
    }
})
