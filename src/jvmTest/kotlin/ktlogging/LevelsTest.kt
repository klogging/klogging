package ktlogging

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import ktlogging.events.Level
import ktlogging.events.LogEvent

class LevelsTestLogger(private val level: Level) : KtLogger {
    override val name = "LevelsTestLogger"

    override fun minLevel() = level

    var loggedMessage: Any? = null

    override suspend fun logMessage(level: Level, exception: Exception?, event: Any?) {
        loggedMessage = event
    }

    override suspend fun e(template: String, vararg values: Any?): LogEvent {
        TODO("Not yet implemented")
    }
}

class LevelsTest : DescribeSpec({
    describe("at all logger levels") {
        it("`log()` calls `logMessage()` for all levels") {
            Level.values().forEach { loggerLevel ->
                val logger = LevelsTestLogger(loggerLevel)
                Level.values().forEach { eventLevel ->
                    randomString().let { msg ->
                        logger.log(eventLevel, msg)
                        logger.loggedMessage shouldBe msg
                    }
                }
            }
        }
    }
})