package io.klogging.eventordering

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.logger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.random.Random

val logger = logger("io.klogging.internal.EventOrdering")

suspend fun main() {
    loggingConfiguration(append = false) {
        kloggingMinLogLevel = Level.TRACE
        sink("console", TimestampString(), FileSink())
        logging {
            fromMinLevel(Level.INFO) {
                toSink("console")
            }
        }
    }
    coroutineScope {
        logger.info("Starting")
        (1..50)
            .map { counter ->
                async { doThing(counter) }
            }.awaitAll()
        logger.info("Finished")
        delay(500)
    }
}

private suspend fun doThing(counter: Int): Int {
    delay(Random.nextLong(40, 100))
    logger.info("Counter: {counter}", counter)
    return counter
}
