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

package io.klogging.templating

import io.klogging.randomString
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

class TemplatingTest : DescribeSpec({
    describe("extracting item names") {
        it("returns empty list if there are no holes in the template") {
            extractItemNames("There are no holes in this template") shouldBe listOf()
        }
        it("returns the name of an item at the start of a template") {
            val name = randomString()
            extractItemNames("{$name} was at the start") shouldContainInOrder listOf(name)
        }
        it("returns the name of an item in the middle of a template") {
            val name = randomString()
            extractItemNames("This {$name} is in the middle") shouldContainInOrder listOf(name)
        }
        it("returns the name of an item at the end of a template") {
            val name = randomString()
            extractItemNames("At the end is the {$name}") shouldContainInOrder listOf(name)
        }
        it("returns the name all items in a template") {
            val names = listOf(randomString(), randomString(), randomString())
            extractItemNames(
                "One: {${names[0]}}, two: {${names[1]}} and three: {${names[2]}}",
            ) shouldContainInOrder names
        }
        it("returns item names that start with $ or @") {
            val names = listOf("item", "\$item", "@item")
            extractItemNames(
                "One: {${names[0]}}, two: {${names[1]}} and three: {${names[2]}}",
            ) shouldContainInOrder names
        }
        it("ignores any names in double braces like {{this}}") {
            extractItemNames("This {{name}} is not a hole") shouldBe listOf()
            extractItemNames("Escaped brace {{ and {a} hole") shouldBe listOf("a")
        }
    }

    describe("template items") {
        it("returns nothing if there are no holes in the template") {
            val template = randomString()
            templateItems(template) shouldHaveSize 0
        }
        it("extracts a single named hole using the first supplied item") {
            val template = "User {Name} logged in"
            val name = randomString()
            val items = templateItems(template, name)

            items shouldContain ("Name" to name)
        }
        it("extracts multiple holes in a template") {
            val testTemplate = "User {Name} logged in from {IpAddress} at {LoginTime}"

            val items = templateItems(testTemplate, "Fred", "192.168.1.1", 1626091790484)
            items shouldContainExactly mapOf(
                "Name" to "Fred",
                "IpAddress" to "192.168.1.1",
                "LoginTime" to 1626091790484,
            )
        }
        it("ignores extra items provided") {
            val items = templateItems("User {Name} logged in", "Joe", "192.168.2.2")
            items shouldBe mapOf("Name" to "Joe")
        }
        it("only extracts provided items") {
            val items = templateItems("User {Name} logged in from {IpAddress}", "Sue")
            items shouldBe mapOf("Name" to "Sue")
        }
        it("includes names that start with $ or @") {
            val items = templateItems("Item {\$item} found at {@coords}", "Fred", Pair(-27.51, 153.04))
            items shouldContainExactly mapOf("\$item" to "Fred", "@coords" to Pair(-27.51, 153.04))
        }
    }
})
