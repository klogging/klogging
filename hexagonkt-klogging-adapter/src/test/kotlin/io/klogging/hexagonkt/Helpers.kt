/*

   Copyright 2021-2024 Michael Strasser.

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

package io.klogging.hexagonkt

import io.klogging.Level
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.events.LogEvent
import io.klogging.sending.EventSender
import kotlin.random.Random
import kotlin.random.nextULong

/**
 * Random string to use in tests where the value is opaque.
 * It is suitable for when you don't care what the value is
 * or where you test that the value has been copied somewhere.
 */
fun randomString() = Random.nextULong().toString(16)

fun eventSaver(saved: MutableList<LogEvent>): EventSender =
    { batch: List<LogEvent> -> saved.addAll(batch) }

/**
 * Configuration that saves all logged events into a list for checking by tests.
 */
fun savedEvents(append: Boolean = false, logDirect: Boolean = true): MutableList<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    loggingConfiguration(append) {
        kloggingMinLogLevel(Level.ERROR)
        if (logDirect) minDirectLogLevel(Level.TRACE)
        sink("test", SinkConfiguration(eventSender = eventSaver(saved)))
        logging { fromMinLevel(Level.TRACE) { toSink("test") } }
    }
    return saved
}
