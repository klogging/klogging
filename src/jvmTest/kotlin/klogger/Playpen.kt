package klogger

import klogger.clef.clef
import klogger.clef.sendClef
import klogger.context.logContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.UUID

fun main() = runBlocking {

    Logging.addSender { e ->
        val message = clef(e)
        sendClef(message)
    }

    val logger = BaseLogger("main")
    launch(logContext("run" to UUID.randomUUID().toString())) {
        logger.info("Start")
        repeat(5) { c ->
            logger.info(">> ${c + 1}")
            launch(logContext("counter" to c.toString())) {
                repeat(5) { i ->
                    logger.info("Event ${i + 1} at ${Instant.now()}")
                }
            }
            logger.info("<< ${c + 1}")
        }
        logger.info("Finish")
    }
    logger.info("All done")
}

