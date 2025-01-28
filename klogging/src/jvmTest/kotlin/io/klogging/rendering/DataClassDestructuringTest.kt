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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

data class User(val name: String, val age: Int)
data class Coords(val x: Double, val y: Double)
data class Valid(val valid: Boolean, val reason: String)
data class Nullable(val name: String, val age: Int?)

class DataClassDestructuringTest : DescribeSpec({
    describe("parseValue() function") {
        it("parses an integer as type Int") {
            checkAll(Arb.int()) { int ->
                parseValue("$int").shouldBeInstanceOf<Int>()
            }
        }
        it("parses a double as type Double") {
            checkAll(Arb.double()) { double ->
                parseValue("$double").shouldBeInstanceOf<Double>()
            }
        }
        it("""parses "null" as null""") {
            parseValue("null").shouldBeNull()
        }
    }
    describe("Destructuring data classes") {
        it("with string and integer properties") {
            destructure(User("Derek", 42)) shouldBe
                    """{"name":"Derek","age":42,"${'$'}type":"User"}"""
        }
        it("with decimal property") {
            destructure(Coords(-27.51, 153.04)) shouldBe
                    """{"x":-27.51,"y":153.04,"${'$'}type":"Coords"}"""
        }
        it("with boolean and string properties") {
            destructure(Valid(true, "Good stuff")) shouldBe
                    """{"valid":true,"reason":"Good stuff","${'$'}type":"Valid"}"""
            destructure(Valid(false, "You lied to me")) shouldBe
                    """{"valid":false,"reason":"You lied to me","${'$'}type":"Valid"}"""
        }
        it("with null value") {
            destructure(Nullable("Fred", null)) shouldBe
                    """{"name":"Fred","age":null,"${'$'}type":"Nullable"}"""
        }
    }
})
