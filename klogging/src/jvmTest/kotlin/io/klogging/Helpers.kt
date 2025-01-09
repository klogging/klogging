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

import io.klogging.Level.TRACE
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.klogging.events.hostname
import io.klogging.events.timestampNow
import io.klogging.sending.EventSender
import io.kotest.property.arbitrary.next
import kotlinx.datetime.Instant
import kotlin.io.path.Path
import kotlin.random.Random
import kotlin.random.nextULong

/**
 * Random string to use in tests where the value is opaque.
 * It is suitable for when you don't care what the value is
 * or where you test that the value has been copied somewhere.
 *
 * @return a short, random string
 */
fun randomString() = Random.nextULong().toString(16)

fun logEvent(
    timestamp: Instant = timestampNow(),
    host: String = hostname,
    logger: String = genLoggerName.next(),
    context: String? = null,
    level: Level = genLevel.toArb().next(),
    template: String? = null,
    message: String = genMessage.next(),
    stackTrace: String? = null,
    items: EventItems = mapOf(),
) = LogEvent(
    timestamp = timestamp,
    host = host,
    logger = logger,
    context = context,
    level = level,
    template = template,
    message = message,
    stackTrace = stackTrace,
    items = items,
)

fun eventSaver(saved: MutableList<LogEvent>): EventSender = object : EventSender {
    override fun invoke(batch: List<LogEvent>) {
        saved.addAll(batch)
    }
}

/**
 * Configuration that saves all logged events into a list for checking by tests.
 *
 * @param append append this configuration to the existing one
 * @param logDirect send all log events directly
 * @return list of saved log events
 */
fun savedEvents(append: Boolean = false, logDirect: Boolean = true): List<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    loggingConfiguration(append) {
        if (logDirect) minDirectLogLevel(TRACE)
        sink("test", SinkConfiguration(eventSender = eventSaver(saved)))
        logging { fromMinLevel(TRACE) { toSink("test") } }
    }
    return saved
}

/**
 * Absolute path of a file relative to the fixtures directory.
 */
fun fixturePath(fixturePath: String): String? = System.getProperty("user.dir")?.let { userDir ->
    Path(userDir, "src/jvmTest/fixtures", fixturePath).toString()
}
