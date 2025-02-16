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

import io.klogging.events.EventItems
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainExactly

class DestructuringTest : DescribeSpec({
    describe("Destructuring classes into maps using reflection") {
        it("data class with string and integer properties") {
            data class User(val name: String, val age: Int)
            destructure(User("Derek", 42)) shouldContainExactly
                    mapOf("name" to "Derek", "age" to 42, typeKey to "User")
        }
        it("data class with floating point properties") {
            data class Coords(val x: Double, val y: Double)
            destructure(Coords(-27.51, 153.04)) shouldContainExactly
                    mapOf("x" to -27.51, "y" to 153.04, typeKey to "Coords")
        }
        it("data class with boolean property") {
            data class Valid(val valid: Boolean, val reason: String)
            destructure(Valid(true, "Good stuff")) shouldContainExactly
                    mapOf("valid" to true, "reason" to "Good stuff", typeKey to "Valid")
            destructure(Valid(false, "You lied to me")) shouldContainExactly
                    mapOf("valid" to false, "reason" to "You lied to me", typeKey to "Valid")
        }
        it("with null value") {
            data class Nullable(val name: String, val age: Int?)
            destructure(Nullable("Fred", null)) shouldContainExactly
                    mapOf("name" to "Fred", "age" to null, typeKey to "Nullable")
        }
        it("with nested data classes") {
            data class User(val name: String, val age: Int)
            data class Login(val user: User, val source: String)
            destructure(Login(User("Fred", 21), "Mobile")) shouldContainExactly
                    mapOf(
                        "user" to mapOf("name" to "Fred", "age" to 21, typeKey to "User"),
                        "source" to "Mobile",
                        typeKey to "Login"
                    )
        }
        it("with ordinary class") {
            class ComplexClass(val name: String, val length: Double, val angle: Int = 0)
            destructure(ComplexClass("thing", 5.123)) shouldContainExactly
                    mapOf("name" to "thing", "length" to 5.123, "angle" to 0, typeKey to "ComplexClass")
        }
        it("data class with calculated property") {
            data class Contact(val firstName: String, val lastName: String) {
                val fullName: String get() = "$firstName $lastName"
            }
            destructure(Contact("William", "Johnson")) shouldContainExactly
                    mapOf(
                        "firstName" to "William",
                        "lastName" to "Johnson",
                        "fullName" to "William Johnson",
                        typeKey to "Contact"
                    )
        }
        it("data class with list property") {
            data class Thing(val names: List<String>)
            destructure(Thing(listOf("one", "two"))) shouldContainExactly
                    mapOf("names" to listOf("one", "two"), typeKey to "Thing")
        }
    }
    describe("EventItems.destructured extension property") {
        it("ignores any items without destructuring indicators") {
            val items: EventItems = mapOf("name" to "Joe", "age" to 23)
            items.destructured shouldContainExactly
                    mapOf("name" to "Joe", "age" to 23)
        }
        it("strips leading `@` from keys that indicate destructuring") {
            data class User(val name: String, val age: Int)
            val items: EventItems = mapOf("@user" to User("Olive", 8))
            items.destructured.keys shouldContainExactly setOf("user")
        }
        it("replaces values with a destructuring map when keys start with `@`") {
            data class User(val name: String, val age: Int)
            val items: EventItems = mapOf("@user" to User("Mabel", 4))
            items.destructured shouldContainExactly mapOf("user" to mapOf(
                "name" to "Mabel",
                "age" to 4,
                typeKey to "User"
            ))
        }
    }
})
