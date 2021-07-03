package klogger

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import klogger.events.Level

fun testLogger(
    level: Level = Level.FATAL
) = object : Klogger {

    override fun minLevel() = level

    override suspend fun log(level: Level, message: String) {
        TODO("Not yet implemented")
    }

}

class KloggerTest : DescribeSpec({

    describe("Klogger") {
        describe("level check") {
            it("isTraceEnabled() is true only for TRACE") {
                testLogger(Level.TRACE).isTraceEnabled() shouldBe true
                testLogger(Level.DEBUG).isTraceEnabled() shouldBe false
                testLogger(Level.INFO).isTraceEnabled() shouldBe false
                testLogger(Level.WARN).isTraceEnabled() shouldBe false
                testLogger(Level.ERROR).isTraceEnabled() shouldBe false
                testLogger(Level.FATAL).isTraceEnabled() shouldBe false
            }
            it("isDebugEnabled() is true only for TRACE and DEBUG") {
                testLogger(Level.TRACE).isDebugEnabled() shouldBe true
                testLogger(Level.DEBUG).isDebugEnabled() shouldBe true
                testLogger(Level.INFO).isDebugEnabled() shouldBe false
                testLogger(Level.WARN).isDebugEnabled() shouldBe false
                testLogger(Level.ERROR).isDebugEnabled() shouldBe false
                testLogger(Level.FATAL).isDebugEnabled() shouldBe false
            }
            it("isInfoEnabled() is true only for TRACE, DEBUG and INFO") {
                testLogger(Level.TRACE).isInfoEnabled() shouldBe true
                testLogger(Level.DEBUG).isInfoEnabled() shouldBe true
                testLogger(Level.INFO).isInfoEnabled() shouldBe true
                testLogger(Level.WARN).isInfoEnabled() shouldBe false
                testLogger(Level.ERROR).isInfoEnabled() shouldBe false
                testLogger(Level.FATAL).isInfoEnabled() shouldBe false
            }
        }
    }

})