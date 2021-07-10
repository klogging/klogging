package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class KtLoggingTest : DescribeSpec({
    describe("logger name") {
        it("can be set explicitly") {
            logger("LoggerName").name shouldBe "LoggerName"
        }
        it("is the full name of the class when it implements KtLogging") {
            ImplementingClass().logger.name shouldBe "klogger.ImplementingClass"
        }
        it("is the full name of the class when its companion object implements KtLogging") {
            ClassWithImplementingCompanion.logger.name shouldBe "klogger.ClassWithImplementingCompanion"
        }
        it("is the full name of the class that creates a property using the logger() function") {
            ClassWithLoggerProperty().logger.name shouldBe "klogger.ClassWithLoggerProperty"
        }
        it("is the full name of the class with a companion object that creates a property using the logger() function") {
            ClassWithLoggerInCompanion.logger.name shouldBe "klogger.ClassWithLoggerInCompanion"
        }
    }
})

class ImplementingClass : KtLogging

class ClassWithImplementingCompanion {
    companion object : KtLogging
}

class ClassWithLoggerProperty {
    val logger = logger(this::class)
}

class ClassWithLoggerInCompanion {
    companion object {
        val logger = logger(ClassWithLoggerInCompanion::class)
    }
}
