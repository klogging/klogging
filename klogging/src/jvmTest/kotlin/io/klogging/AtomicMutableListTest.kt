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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import mjs.kotest.description

class AtomicMutableListTest : DescribeSpec({
    description("Multiplatform, thread-safe mutable list, implemented using AtomicFU")

    describe("with empty constructor") {
        it("returns size and elements") {
            val list = AtomicMutableList<String>()
            list.size shouldBe 0
            list.toList() shouldBe emptyList()
        }
        it("checks contents and elements") {
            checkAll(genString) { element ->
                val list = AtomicMutableList<String>()
                list.isEmpty() shouldBe true
                list.contains(element) shouldBe false
            }
        }
        it("throws `IndexOutOfBoundsException` when trying to get an element by index") {
            checkAll(Arb.int()) { index ->
                shouldThrow<IndexOutOfBoundsException> {
                    AtomicMutableList<String>()[index]
                }
            }
        }
        it("returns false when trying to remove an element") {
            checkAll(genString) { element ->
                AtomicMutableList<String>().remove(element) shouldBe false
            }
        }
        it("throws `IndexOutOfBoundsException` when trying to remove an element by index") {
            checkAll(Arb.int()) { index ->
                shouldThrow<IndexOutOfBoundsException> {
                    AtomicMutableList<String>().removeAt(index)
                }
            }
        }
        it("adds elements") {
            checkAll(genString) { element ->
                val list = AtomicMutableList<String>()
                list.add(element)
                list.isEmpty() shouldBe false
                list.first() shouldBe element
            }
        }
        it("adds elements from another list") {
            checkAll(Arb.list(genString, 0..50)) { elements ->
                val list = AtomicMutableList<String>()
                list.addAll(elements)
                elements.forEachIndexed { index, element ->
                    list[index] shouldBe element
                }
            }
        }
    }

    describe("with elements provided to constructor") {
        it("returns size and elements") {
            checkAll(Arb.list(genString, 1..10)) { elements ->
                val list = AtomicMutableList(*elements.toTypedArray())
                list.size shouldBe elements.size
                list shouldBe elements
            }
        }
        it("checks contents") {
            checkAll(Arb.list(genString, 1..10)) { elements ->
                val list = AtomicMutableList(*elements.toTypedArray())
                list.isEmpty() shouldBe false
                elements.forEach { list.contains(it) shouldBe true }
            }
        }
        it("gets elements") {
            checkAll(Arb.list(genString, 0..99)) { elements ->
                val list = AtomicMutableList(*elements.toTypedArray())
                list.size shouldBe elements.size
                elements.forEachIndexed { index, element -> list[index] shouldBe element }
            }
        }
        it("clears the list") {
            checkAll(Arb.list(genString, 0..99)) { elements ->
                val list = AtomicMutableList(*elements.toTypedArray())
                list.clear()
                list.isEmpty() shouldBe true
            }
        }
        it("adds an element to a list") {
            checkAll(Arb.list(genString, 10..19), genString) { elements, newElement ->
                val list = AtomicMutableList(*elements.toTypedArray())
                list.add(newElement) shouldBe true
                list.size shouldBe elements.size + 1
            }
        }
        it("adds an element to a list at an index") {
            checkAll(
                Arb.list(genString, 10..19),
                genString,
                Arb.int(0..9),
            ) { elements, newElement, index ->
                val list = AtomicMutableList(*elements.toTypedArray())
                list.add(index, newElement)
                list.size shouldBe elements.size + 1
                list[index] shouldBe newElement
            }
        }
        it("adds a collection to a list") {
            checkAll(Arb.list(genString, 10..19), Arb.list(genString, 10..19)) { elts1, elts2 ->
                val list = AtomicMutableList(*elts1.toTypedArray())
                list.addAll(emptyList()) shouldBe false
                list.addAll(elts2) shouldBe true
            }
        }
        it("removes an element") {
            checkAll(Arb.list(genString, 10..19)) { elements ->
                val list = AtomicMutableList(*elements.toTypedArray())
                elements.forEach { list.remove(it) shouldBe true }
            }
        }
        it("removes an element by index") {
            checkAll(Arb.list(genString, 10..19)) { elements ->
                val list = AtomicMutableList(*elements.toTypedArray())
                (9 downTo 0).forEach { index ->
                    list.removeAt(index) shouldBe elements[index]
                }
            }
        }
        it("updates an element by index") {
            checkAll(Arb.list(genString, 10..19), Arb.list(genString, 1..9)) { elts1, elts2 ->
                val list = AtomicMutableList(*elts1.toTypedArray())
                elts2.forEachIndexed { index, element ->
                    list.set(index, element) shouldBe elts1[index]
                }
            }
        }
    }
})
