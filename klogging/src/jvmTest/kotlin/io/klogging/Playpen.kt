/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging

import io.klogging.config.getenv
import io.klogging.config.loggingConfiguration
import io.klogging.context.Context
import io.klogging.context.logContext
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RenderPattern
import io.klogging.rendering.RenderString
import io.klogging.rendering.evalTemplate
import io.klogging.rendering.renderHec
import io.klogging.sending.ElkEndpoint
import io.klogging.sending.STDOUT
import io.klogging.sending.SendElk
import io.klogging.sending.SendString
import io.klogging.sending.seqServer
import io.klogging.sending.splunkServer
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.util.UUID.randomUUID
import kotlin.random.Random

fun localNow(): LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

/**
 * Main program for experimenting with Klogging features as they are developed.
 */
suspend fun main() = coroutineScope {
    val logger = logger("io.klogging.example.KloggerPlaypen", "source" to "Playpen")
    Context.addBaseContext("app" to "Playpen")

    if (System.getenv("RENDER_PATTERN") == "true") {
        loggingConfiguration(append = false) {
            sink(
                "console",
                RenderPattern("%t{LOCAL_TIME} %-5v [%-20c] - %20l - %m - %i%2s"),
                STDOUT
            )
            logging {
                fromMinLevel(Level.TRACE) { toSink("console") }
            }
        }
    }

    noCoLogger("OneOffLogger", logger).info("Here it is", mapOf("black" to "white", "up" to "down"))

    if (System.getenv("LOCAL_SPLUNK") == "true") {
        loggingConfiguration(append = true) {
            sink(
                "splunk",
                renderHec(source = "Playpen"),
                splunkServer(
                    hecUrl = "https://localhost:8088",
                    hecToken = getenv("SPLUNK_HEC_TOKEN")!!,
                    checkCertificate = false,
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
                RENDER_CLEF,
                seqServer(
                    url = getenv("SEQ_URL")!!,
                    apiKey = getenv("SEQ_API_KEY"),
                    checkCertificate = false,
                ),
            )
            logging {
                toSink("seq")
            }
        }
    }

    if (System.getenv("ELK_CONFIG") == "true") {
        loggingConfiguration {
            sink(
                "elk",
                SendElk(
                    ElkEndpoint(
                        url = getenv("ELK_URL")!!,
                        checkCertificate = false,
                    )
                )
            )
        }
    }

    if (System.getenv("FUNC_INTERFACE_CONFIG") == "true") {
        val logFile = File("/tmp/playpen.log")
        val backwards = RenderString { it.evalTemplate().reversed() }
        val tempFile = SendString { logFile.appendText("$it\n") }

        loggingConfiguration(append = true) {
            sink("tempLog", backwards, tempFile)
            logging { toSink("tempLog") }
        }
    }

    val outerCount = 3
    val innerCount = 3

    val run = randomUUID()
    launch(logContext("run" to run) + CoroutineName("Playpen")) {
        logger.info { "Start" }
        repeat(outerCount) { c ->
            logger.info { e(">> {Counter}", c + 1) }
            launch(logContext("Counter" to (c + 1))) {
                repeat(innerCount) { i ->
                    logger.info("Event {Iteration} at {RightNow}", i + 1, localNow())
                }
            }
            delay(Random.nextLong(100))
            logger.info { e("<< {Counter}", c + 1) }
            functionWithException(logger)
        }
        logger.info { "Finish" }
    }
    // There must be at least one statement outside the coroutine scope.
    logger.info("All done", mapOf("run" to run))
    delay(500)
}

suspend fun functionWithException(logger: Klogger) {
    try {
        throw RuntimeException("Oops! Something went wrong")
    } catch (e: Exception) {
        logger.warn(e) { e("Message: {Message}", e.message) }
    }
}
