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

import io.klogging.Level.TRACE
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.events.LogEvent
import io.klogging.events.hostname
import io.klogging.events.timestampNow
import io.klogging.rendering.RenderString
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.random.nextULong

fun randomLoggerName() = Random.nextInt().toString(16)

/**
 * Random string to use in tests where the value is opaque.
 * It is suitable for when you don't care what the value is
 * or where you test that the value has been copied somewhere.
 */
fun randomString() = Random.nextULong().toString(16)
fun randomLevel() = Level.values().random()

fun logEvent(
    timestamp: Instant = timestampNow(),
    host: String = hostname,
    name: String = randomLoggerName(),
    level: Level = randomLevel(),
    message: String = randomString(),
    stackTrace: String? = null,
    items: Map<String, Any?> = mapOf(),
) = LogEvent(
    timestamp = timestamp,
    host = host,
    logger = name,
    level = level,
    message = message,
    stackTrace = stackTrace,
    items = items,
)

/** Crude way to help ensure coroutine processing is complete in tests. */
suspend fun waitForSend(millis: Long = 50) = delay(millis)

fun savedEvents(): MutableList<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    val saveEventRenderer: RenderString = { e -> saved.add(e); "" }
    loggingConfiguration {
        sink("test", SinkConfiguration(saveEventRenderer) {})
        logging { fromMinLevel(TRACE) { toSink("test") } }
    }
    return saved
}
