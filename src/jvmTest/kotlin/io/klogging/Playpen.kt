/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging

import io.klogging.config.loggingConfiguration
import io.klogging.config.seq
import io.klogging.context.logContext
import io.klogging.events.Level
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

/**
 * Main program for experimenting with Klogging features as they are developed.
 */
fun main() = runBlocking {

    loggingConfiguration {
        sink("seq", seq("http://localhost:5341"))
        logging {
            exactLogger("Playpen")
            fromMinLevel(Level.INFO) {
                toSink("seq")
            }
        }
    }

    val logger = logger("Playpen")
    launch(logContext("run" to UUID.randomUUID())) {
        logger.info { "Start" }
        repeat(2) { c ->
            logger.info { e(">> {Counter}", c + 1) }
            launch(logContext("Counter" to (c + 1))) {
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

suspend fun functionWithException(logger: Klogger) {
    try {
        throw RuntimeException("Oops! Something went wrong")
    } catch (e: Exception) {
        logger.warn(e) { e("Message: {Message}", e.message) }
    }
}
