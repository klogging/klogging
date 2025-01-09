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
import io.kotest.matchers.shouldBe

@Suppress("ktlint:max-line-length")
class KloggingTest : DescribeSpec({
    describe("logger name") {
        it("can be set explicitly") {
            logger("LoggerName").name shouldBe "LoggerName"
        }
        it("is the full name of the class when it implements Klogging") {
            ImplementingClass().logger.name shouldBe "io.klogging.ImplementingClass"
        }
        it("is the full name of the class when its companion object implements Klogging") {
            ClassWithImplementingCompanion.logger.name shouldBe "io.klogging.ClassWithImplementingCompanion"
        }
        it("is the full name of the class when its named companion object implements Klogging") {
            ClassWithNamedImplementingCompanion.logger.name shouldBe "io.klogging.ClassWithNamedImplementingCompanion"
        }
        it("is the full name of the class that creates a property using the logger() function") {
            ClassWithLoggerProperty().logger.name shouldBe "io.klogging.ClassWithLoggerProperty"
        }
        it("is the full name of the class with a companion object that creates a property using the logger() function") {
            ClassWithLoggerInCompanion.logger.name shouldBe "io.klogging.ClassWithLoggerInCompanion"
        }
        it("is the full name of the reified class used in a call to generic `logger()` function") {
            logger<ClassUsedInCallWithReifiedGeneric>().name shouldBe
                "io.klogging.ClassUsedInCallWithReifiedGeneric"
        }
    }
})

class ImplementingClass : Klogging

class ClassWithImplementingCompanion {
    companion object : Klogging
}

class ClassWithNamedImplementingCompanion {
    companion object NamedCompanion : Klogging
}

class ClassWithLoggerProperty {
    val logger = logger(this::class)
}

class ClassWithLoggerInCompanion {
    companion object {
        val logger = logger(ClassWithLoggerInCompanion::class)
    }
}

class ClassUsedInCallWithReifiedGeneric
