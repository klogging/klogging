package io.klogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

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
        it("is the full name of the class that creates a property using the logger() function") {
            ClassWithLoggerProperty().logger.name shouldBe "io.klogging.ClassWithLoggerProperty"
        }
        it("is the full name of the class with a companion object that creates a property using the logger() function") {
            ClassWithLoggerInCompanion.logger.name shouldBe "io.klogging.ClassWithLoggerInCompanion"
        }
    }
})

class ImplementingClass : Klogging

class ClassWithImplementingCompanion {
    companion object : Klogging
}

class ClassWithLoggerProperty {
    val logger = logger(this::class)
}

class ClassWithLoggerInCompanion {
    companion object {
        val logger = logger(ClassWithLoggerInCompanion::class)
    }
}
