package io.klogging.eventordering

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.logger
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select
import kotlin.random.Random

val counter = atomic(0L)
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
        select {
            List(10) {
                async { doThing() }.onAwait { it }
            }
        }

        delay(500)
    }
}

private suspend fun doThing(): String {
    delay(Random.nextLong(20, 100))
    val count = counter.incrementAndGet()
    logger.info("Count: {count}", count)
    return count.toString()
}
