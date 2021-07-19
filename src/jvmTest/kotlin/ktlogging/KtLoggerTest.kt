package ktlogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import ktlogging.events.Level
import ktlogging.events.LogEvent

class TestLogger(
    private val level: Level = Level.FATAL
) : KtLogger {

    override val name: String = "TestLogger"

    internal var logged: String? = null

    override fun minLevel() = level
    override suspend fun logMessage(level: Level, exception: Exception?, event: Any?) {
        logged = event.toString()
    }

    override suspend fun e(template: String, vararg values: Any?): LogEvent {
        TODO("Not yet implemented")
    }
}

class KtLoggerTest : DescribeSpec({

    describe("KtLogger") {
        describe("level check") {
            it("isTraceEnabled() is true only for TRACE") {
                TestLogger(Level.TRACE).isTraceEnabled() shouldBe true
                TestLogger(Level.DEBUG).isTraceEnabled() shouldBe false
                TestLogger(Level.INFO).isTraceEnabled() shouldBe false
                TestLogger(Level.WARN).isTraceEnabled() shouldBe false
                TestLogger(Level.ERROR).isTraceEnabled() shouldBe false
                TestLogger(Level.FATAL).isTraceEnabled() shouldBe false
            }
            it("isDebugEnabled() is true only for TRACE and DEBUG") {
                TestLogger(Level.TRACE).isDebugEnabled() shouldBe true
                TestLogger(Level.DEBUG).isDebugEnabled() shouldBe true
                TestLogger(Level.INFO).isDebugEnabled() shouldBe false
                TestLogger(Level.WARN).isDebugEnabled() shouldBe false
                TestLogger(Level.ERROR).isDebugEnabled() shouldBe false
                TestLogger(Level.FATAL).isDebugEnabled() shouldBe false
            }
            it("isInfoEnabled() is true only for TRACE, DEBUG and INFO") {
                TestLogger(Level.TRACE).isInfoEnabled() shouldBe true
                TestLogger(Level.DEBUG).isInfoEnabled() shouldBe true
                TestLogger(Level.INFO).isInfoEnabled() shouldBe true
                TestLogger(Level.WARN).isInfoEnabled() shouldBe false
                TestLogger(Level.ERROR).isInfoEnabled() shouldBe false
                TestLogger(Level.FATAL).isInfoEnabled() shouldBe false
            }
        }
    }

})
