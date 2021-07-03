package klogger

import klogger.context.logContext
import klogger.gelf.gelf
import klogger.gelf.send
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.UUID

fun main() = runBlocking {

    eventSender = { e ->
//        val ts = Instant.ofEpochSecond(e.timestamp.epochSeconds, e.timestamp.nanos)
//        println("$ts ${e.items} ${e.template}")
        val message = gelf(e)
        println(message)
        send(message)
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
            logger.info("<< $c")
        }
        logger.info("Finish")
    }
    logger.info("All done")
}

