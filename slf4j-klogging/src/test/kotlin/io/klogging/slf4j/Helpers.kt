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

package io.klogging.slf4j

import io.klogging.Level
import io.klogging.Level.TRACE
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.events.LogEvent
import io.klogging.sending.EventSender
import kotlin.random.Random
import kotlin.random.nextULong

/** A random string used in testing. */
fun randomString() = Random.nextULong().toString(16)

/**
 * Implementation of [EventSender] that saves log events into the supplied mutable list.
 *
 * @param saved list where log events will be saved
 * @return the [EventSender] implementation
 */
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
fun savedEvents(
    minLevel: Level = TRACE,
    append: Boolean = false,
    logDirect: Boolean = true,
): List<LogEvent> {
    val saved: MutableList<LogEvent> = mutableListOf()
    loggingConfiguration(append) {
        kloggingMinLogLevel(Level.ERROR)
        if (logDirect) minDirectLogLevel(TRACE)
        sink("test", SinkConfiguration(eventSender = eventSaver(saved)))
        logging { fromMinLevel(minLevel) { toSink("test") } }
    }
    return saved
}
