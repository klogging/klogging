package io.klogging.eventorder

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.logger
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.random.Random

val logger = logger("io.klogging.eventorder.EventOrder")

suspend fun main() {
    loggingConfiguration(append = false) {
        kloggingMinLogLevel = Level.FATAL
        sink("console", TimestampString(), FileSink())
        logging {
            fromMinLevel(Level.INFO) {
                toSink("console")
            }
        }
    }
    coroutineScope {
        (1..10)
            .map { counter ->
                async { doThing(counter) }
            }.awaitAll()
        // Ensure all events are processed before exiting
        delay(500)
    }
}

private suspend fun doThing(counter: Int): Int {
    delay(Random.nextLong(40, 100))
    logger.info("Counter: {counter}", counter)
    return counter
}
