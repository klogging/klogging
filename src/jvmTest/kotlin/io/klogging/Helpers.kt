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

import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.events.Timestamp
import io.klogging.events.hostname
import io.klogging.render.RenderString
import kotlinx.coroutines.delay
import java.time.Instant
import kotlin.random.Random
import kotlin.random.nextULong

fun timestampNow() = Instant.now().let { Timestamp(it.epochSecond, it.nano.toLong()) }

fun randomLoggerName() = Random.nextInt().toString(16)

fun randomString() = Random.nextULong().toString(16)

fun randomLevel() = Level.values().random()

fun logEvent(
    timestamp: Timestamp = timestampNow(),
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

suspend fun waitForDispatch(millis: Long = 50) = delay(millis)

fun savedEvents(): MutableList<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    val saveEventRenderer: RenderString = { e -> saved.add(e); "" }
    loggingConfiguration {
        sink("test", SinkConfiguration(saveEventRenderer) {})
        logging { fromMinLevel(Level.TRACE) { toSink("test") } }
    }
    return saved
}
