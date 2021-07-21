package ktlogging

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktlogging.clef.dispatchClef
import ktlogging.clef.toClef
import ktlogging.config.DEFAULT_CONSOLE
import ktlogging.config.LogDispatcher
import ktlogging.config.LoggingConfig
import ktlogging.config.LoggingConfiguration
import ktlogging.context.logContext
import ktlogging.events.Level
import ktlogging.events.LogEvent
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

fun main() = runBlocking {

    fun LogEvent.format(fmt: String) =
        LogEvent(
            timestamp, host, logger, level, template, message, stackTrace,
            items + mapOf("format" to fmt, "thread" to Thread.currentThread().name)
        )

    LoggingConfiguration.setConfigs(
        DEFAULT_CONSOLE,
        LoggingConfig(
            "ROOT", Level.INFO,
            listOf(
                LogDispatcher("Seq") { e -> dispatchClef(e.format("CLEF").toClef()) }
            )
        ),
    )

    val logger = logger("main")
    launch(logContext("run" to UUID.randomUUID().toString())) {
        logger.info { "Start" }
        repeat(2) { c ->
            logger.info { e(">> {Counter}", c + 1) }
            launch(logContext("Counter" to (c + 1).toString())) {
                repeat(2) { i ->
                    logger.info {
                        e("Event {Iteration} at {RightNow}", i + 1, LocalDateTime.now(ZoneId.of("Australia/Brisbane")))
                    }
                }
            }
            logger.info { e("<< {Counter}", c + 1) }
            functionWithException(logger)
        }
        logger.info { "Finish" }
    }
    // There must be at least one statement outside the coroutine scope.
    logger.info { "All done" }
}

suspend fun functionWithException(logger: KtLogger) {
    try {
        throw RuntimeException("Oops! Something went wrong")
    } catch (e: Exception) {
        logger.warn(e) { e("Message: {Message}", e.message) }
    }
}
