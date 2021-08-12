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

import io.klogging.Level.INFO
import io.klogging.config.loggingConfiguration
import io.klogging.config.seq
import io.klogging.context.logContext
import io.klogging.dispatching.STDOUT
import io.klogging.rendering.RENDER_SIMPLE
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID.randomUUID

/**
 * Main program for experimenting with Klogging features as they are developed.
 */
suspend fun main() = coroutineScope {
    val logger = logger("Playpen")

    loggingConfiguration {
        sink("stdout", RENDER_SIMPLE, STDOUT)
        sink("seq", seq("http://localhost:5341"))
        logging {
            exactLogger("Playpen")
            toSink("seq")
            fromMinLevel(INFO) {
                toSink("stdout")
            }
        }
    }

    launch(logContext("run" to randomUUID())) {
        logger.info { "Start" }
        repeat(2) { c ->
            logger.info { e(">> {Counter}", c + 1) }
            launch(logContext("Counter" to (c + 1))) {
                repeat(2) { i ->
                    logger.info {
                        e(
                            "Event {Iteration} at {RightNow}", i + 1,
                            LocalDateTime.now(ZoneId.of("Australia/Brisbane"))
                        )
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
