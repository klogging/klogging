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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class JsonTest : DescribeSpec({
    describe("JSON map serializer") {
        it("serializes an empty map") {
            serializeMap(mapOf()) shouldBe "{}"
        }
        it("serializes a map with String values") {
            serializeMap(
                mapOf(
                    "colour" to "black",
                ),
            ) shouldBe """{"colour":"black"}"""
        }
        it("serializes a map with Int values") {
            serializeMap(
                mapOf(
                    "count" to 120,
                ),
            ) shouldBe """{"count":120}"""
        }
        it("serializes a map with Double values") {
            serializeMap(
                mapOf(
                    "size" to 13.42,
                ),
            ) shouldBe """{"size":13.42}"""
        }
        it("serializes a map with mixed values") {
            serializeMap(
                mapOf(
                    "colour" to "black",
                    "count" to 120,
                    "size" to 13.42,
                ),
            ) shouldBe """{"colour":"black","count":120,"size":13.42}"""
        }
        it("serializes a map with lists") {
            serializeMap(
                mapOf(
                    "pets" to listOf("cat", "dog"),
                ),
            ) shouldBe """{"pets":["cat","dog"]}"""
        }
        it("serializes a map with maps") {
            serializeMap(
                mapOf(
                    "items" to mapOf("id" to 12345, "name" to "Fred"),
                ),
            ) shouldBe """{"items":{"id":12345,"name":"Fred"}}"""
        }
    }
})
