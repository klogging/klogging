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

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.DescribeSpec

data class User(val name: String, val age: Int)
data class Coords(val x: Double, val y: Double)
data class Valid(val valid: Boolean, val reason: String)
data class Nullable(val name: String, val age: Int?)
data class Login(val user: User, val source: String)

class DataClassDestructuringTest : DescribeSpec({
    describe("Destructuring data classes") {
        it("with string and integer properties") {
            destructureToJson(User("Derek", 42)) shouldEqualJson
                    """{"name":"Derek","age":42,"${'$'}type":"User"}"""
        }
        it("with decimal property") {
            destructureToJson(Coords(-27.51, 153.04)) shouldEqualJson
                    """{"x":-27.51,"y":153.04,"${'$'}type":"Coords"}"""
        }
        it("with boolean and string properties") {
            destructureToJson(Valid(true, "Good stuff")) shouldEqualJson
                    """{"valid":true,"reason":"Good stuff","${'$'}type":"Valid"}"""
            destructureToJson(Valid(false, "You lied to me")) shouldEqualJson
                    """{"valid":false,"reason":"You lied to me","${'$'}type":"Valid"}"""
        }
        it("with null value") {
            destructureToJson(Nullable("Fred", null)) shouldEqualJson
                    """{"name":"Fred","age":null,"${'$'}type":"Nullable"}"""
        }
        it("with nested data classes") {
            destructureToJson(Login(User("Fred", 21), "Mobile")) shouldEqualJson
                    """{"user":{"name":"Fred","age":21,"${'$'}type":"User"},"source":"Mobile","${'$'}type":"Login"}"""
        }
    }
})
