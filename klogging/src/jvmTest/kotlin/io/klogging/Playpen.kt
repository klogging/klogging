/*

   Copyright 2021-2023 Michael Strasser.

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

import io.klogging.config.SinkConfiguration
import io.klogging.config.getenv
import io.klogging.config.loggingConfiguration
import io.klogging.config.seq
import io.klogging.context.Context
import io.klogging.context.logContext
import io.klogging.events.timestampNow
import io.klogging.rendering.RENDER_CLEF
import io.klogging.sending.SplunkEndpoint
import io.klogging.sending.splunkHec
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID.randomUUID
import kotlin.random.Random

/**
 * Main program for experimenting with Klogging features as they are developed.
 */
suspend fun main() = coroutineScope {
    val logger = logger("io.klogging.example.KloggerPlaypen")
    Context.addBaseContext("app" to "Playpen")

    if (System.getenv("LOCAL_SPLUNK") == "true") {
        loggingConfiguration(append = true) {
            sink(
                "splunk",
                SinkConfiguration(
                    eventSender = splunkHec(
                        SplunkEndpoint(
                            hecUrl = "https://localhost:8088",
                            hecToken = getenv("SPLUNK_HEC_TOKEN")!!,
                            checkCertificate = "false",
                        ),
                    ),
                ),
            )
            logging {
                fromMinLevel(Level.INFO) {
                    toSink("splunk")
                }
            }
        }
    }

    if (System.getenv("NEW_SEQ_CONFIG") == "true") {
        loggingConfiguration {
            sink(
                "seq",
                seq(
                    url = "http://localhost:5341",
                    apiKey = getenv("SEQ_API_KEY"),
                    renderer = RENDER_CLEF,
                ),
            )
            logging {
                toSink("seq")
            }
        }
    }

    val outerCount = 2
    val innerCount = 2

    val run = randomUUID()
    launch(logContext("run" to run) + CoroutineName("Playpen")) {
        logger.info { "Start" }
        repeat(outerCount) { c ->
            logger.info { e(">> {Counter}", c + 1) }
            launch(logContext("Counter" to (c + 1))) {
                repeat(innerCount) { i ->
                    logger.info(
                        "Event {Iteration} at {RightNow}",
                        i + 1,
                        timestampNow().toLocalDateTime(TimeZone.currentSystemDefault()),
                    )
                }
            }
            delay(Random.nextLong(100))
            logger.info { e("<< {Counter}", c + 1) }
            functionWithException(logger)
        }
        logger.info { "Finish" }
    }
    // There must be at least one statement outside the coroutine scope.
    logger.info("All done in {run}", run)
    delay(500)
}

suspend fun functionWithException(logger: Klogger) {
    try {
        throw RuntimeException("Oops! Something went wrong")
    } catch (e: Exception) {
        logger.warn(e) { e("Message: {Message}", e.message) }
    }
}
