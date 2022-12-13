/*

   Copyright 2021-2022 Michael Strasser.

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
import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.klogging.events.hostname
import io.klogging.events.timestampNow
import io.klogging.sending.EventSender
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.random.nextUInt
import kotlin.random.nextULong

fun randomLoggerName() = Random.nextUInt().toString(16)

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
    logger: String = randomLoggerName(),
    context: String? = null,
    level: Level = randomLevel(),
    message: String = randomString(),
    stackTrace: String? = null,
    items: EventItems = mapOf()
) = LogEvent(
    timestamp = timestamp,
    host = host,
    logger = logger,
    context = context,
    level = level,
    message = message,
    stackTrace = stackTrace,
    items = items
)

fun eventSaver(saved: MutableList<LogEvent>): EventSender =
    { batch: List<LogEvent> -> saved.addAll(batch) }

/**
 * Configuration that saves all logged events into a list for checking by tests.
 */
fun savedEvents(): MutableList<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    loggingConfiguration {
        sink("test", SinkConfiguration(eventSender = eventSaver(saved)))
        logging { fromMinLevel(TRACE) { toSink("test") } }
    }
    return saved
}
