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

package io.klogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import mjs.kotest.description

class AtomicMutableMapTest : DescribeSpec({
    description("Multiplatform, thread-safe mutable map, implemented using AtomicFU")

    describe("with empty constructor") {
        it("returns size, keys and values") {
            val map = AtomicMutableMap<String, String>()
            map.size shouldBe 0
            map.keys.shouldBeEmpty()
            map.values.shouldBeEmpty()
        }
        it("checks contents, keys and values") {
            checkAll(genString, genString) { key, value ->
                val map = AtomicMutableMap<String, String>()
                map.isEmpty() shouldBe true
                map.containsKey(key) shouldBe false
                map.containsValue(value) shouldBe false
            }
        }
        it("gets values using `get()`") {
            checkAll(genString) { key ->
                val map = AtomicMutableMap<String, String>()
                map.get(key) shouldBe null
            }
        }
        it("gets values using `[]`") {
            checkAll(genString) { key ->
                val map = AtomicMutableMap<String, String>()
                map[key] shouldBe null
            }
        }
        it("adds an entry using `put()`") {
            checkAll(genString, genString) { key, value ->
                val map = AtomicMutableMap<String, String>()
                map.put(key, value) shouldBe null
                map[key] shouldBe value
            }
        }
        it("adds an entry using `[]`") {
            checkAll(genString, genString) { key, value ->
                val map = AtomicMutableMap<String, String>()
                map[key] = value
                map[key] shouldBe value
            }
        }
        it("adds entries from a map") {
            checkAll(genString, genString, genString) { key, value1, value2 ->
                // Ensure keys are unique
                val map = AtomicMutableMap<String, String>()
                map.putAll(mapOf("A$key" to value1, "B$key" to value2))
                map.size shouldBe 2
                map["A$key"] shouldBe value1
                map["B$key"] shouldBe value2
            }
        }
    }
    describe("with map provided to constructor") {
        it("returns size, keys and values") {
            checkAll(genString, genString) { key, value ->
                val map = AtomicMutableMap(key to value)
                map.size shouldBe 1
                map.keys shouldBe mutableSetOf(key)
                map.values shouldBe mutableListOf(value)
            }
        }
        it("checks contents, keys and values") {
            checkAll(genString, genString) { key, value ->
                val map = AtomicMutableMap(key to value)
                map.isEmpty() shouldBe false
                map.containsKey(key) shouldBe true
                map.containsValue(value) shouldBe true
            }
        }
        it("gets values using `get()`") {
            checkAll(genString, genString, genString) { key, value1, value2 ->
                // Ensure keys are unique
                val map = AtomicMutableMap("A$key" to value1, "B$key" to value2)
                map.size shouldBe 2
                map.get("A$key") shouldBe value1
                map.get("B$key") shouldBe value2
                map.get("C$key") shouldBe null
            }
        }
        it("gets values using `[]`") {
            checkAll(genString, genString, genString) { key, value1, value2 ->
                // Ensure keys are unique
                val map = AtomicMutableMap("A$key" to value1, "B$key" to value2)
                map.size shouldBe 2
                map["A$key"] shouldBe value1
                map["B$key"] shouldBe value2
                map["C$key"] shouldBe null
            }
        }
        it("clears the map") {
            checkAll(genString, genString) { key, value ->
                val map = AtomicMutableMap(key to value)
                map.clear()
                map.isEmpty() shouldBe true
            }
        }
        it("removes an entry by key") {
            checkAll(genString, genString) { key, value ->
                val map = AtomicMutableMap(key to value)
                map.remove(key) shouldBe value
                map.isEmpty() shouldBe true
                map.remove(key) shouldBe null
            }
        }
        it("updates an entry using `put()`") {
            checkAll(genString, genString) { key, value ->
                // Ensure values are unique
                val map = AtomicMutableMap(key to "A$value")
                map.put(key, "B$value") shouldBe "A$value"
                map[key] shouldBe "B$value"
            }
        }
        it("puts extra entries using `put()`") {
            checkAll(genString, genString, genString) { key, value1, value2 ->
                // Ensure keys are unique
                val map = AtomicMutableMap("A$key" to value1)
                map.put("B$key", value2) shouldBe null
                map.size shouldBe 2
                map["A$key"] shouldBe value1
                map["B$key"] shouldBe value2
            }
        }
        it("puts extra entries using `[]`") {
            checkAll(genString, genString, genString) { key, value1, value2 ->
                // Ensure keys are unique
                val map = AtomicMutableMap("A$key" to value1)
                map["B$key"] = value2
                map.size shouldBe 2
                map["A$key"] shouldBe value1
                map["B$key"] shouldBe value2
            }
        }
        it("adds entries from a map") {
            checkAll(genString, genString, genString, genString) { key, value1, value2, value3 ->
                // Ensure keys are unique
                val map = AtomicMutableMap("A$key" to value1)
                map.putAll(mapOf("B$key" to value2, "C$key" to value3))
                map.size shouldBe 3
                map["A$key"] shouldBe value1
                map["B$key"] shouldBe value2
                map["C$key"] shouldBe value3
            }
        }
    }
})
