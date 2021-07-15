package ktlogging

import ktlogging.Dispatcher.setDispatchers
import ktlogging.clef.dispatchClef
import ktlogging.clef.toClef
import ktlogging.context.logContext
import ktlogging.events.LogEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

fun main() = runBlocking {

    fun LogEvent.format(fmt: String) =
        LogEvent(
            id, timestamp, host, logger, level, template, message, stackTrace,
            items + mapOf("format" to fmt, "thread" to Thread.currentThread().name)
        )

    setDispatchers(
        { e -> dispatchClef(e.format("CLEF").toClef()) },
//        { e -> println(e.toClef()) },
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

suspend fun functionWithException(logger: Ktlogger) {
    try {
        throw RuntimeException("Oops! Something went wrong")
    } catch (e: Exception) {
        logger.warn(e) { e("Message: {Message}", e.message) }
    }
}

