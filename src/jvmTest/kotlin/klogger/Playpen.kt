package klogger

import klogger.Dispatcher.setDispatchers
import klogger.clef.dispatchClef
import klogger.clef.toClef
import klogger.context.logContext
import klogger.events.LogEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

fun main() = runBlocking {

    fun LogEvent.format(fmt: String) =
        LogEvent(id, timestamp, host, name, level, message, items + mapOf("format" to fmt))

    setDispatchers(
        { e -> dispatchClef(e.format("CLEF").toClef()) },
    )

    val logger = BaseLogger("main")
    launch(logContext("run" to UUID.randomUUID().toString())) {
        logger.info("Start")
        repeat(2) { c ->
            logger.info(">> ${c + 1}")
            launch(logContext("counter" to (c + 1).toString())) {
                repeat(2) { i ->
                    logger.info("Event ${i + 1} at ${LocalDateTime.now(ZoneId.of("Australia/Brisbane"))}")
                }
            }
            logger.info("<< ${c + 1}")
        }
        logger.info("Finish")
    }
    // There must be at least one statement outside the coroutine scope.
    logger.info("All done")
}

