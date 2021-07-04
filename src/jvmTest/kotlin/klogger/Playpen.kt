package klogger

import klogger.clef.clef
import klogger.clef.sendClef
import klogger.context.logContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

fun main() = runBlocking {

    Logging.addSender { e -> sendClef(clef(e)) }

    val logger = BaseLogger("main")
    launch(logContext("run" to UUID.randomUUID().toString())) {
        logger.info("Start")
        repeat(5) { c ->
            logger.info(">> ${c + 1}")
            launch(logContext("counter" to (c + 1).toString())) {
                repeat(5) { i ->
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

