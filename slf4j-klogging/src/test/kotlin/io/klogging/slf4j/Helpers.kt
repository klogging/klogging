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

package io.klogging.slf4j

import io.klogging.Level
import io.klogging.Level.TRACE
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.events.LogEvent
import io.klogging.sending.EventSender
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextULong

/** A random string used in testing. */
fun randomString() = Random.nextULong().toString(16)

/**
 * Wait for log event dispatch.
 *
 * @param millis number of milliseconds to wait
 */
suspend fun waitForDispatch(millis: Long = 200) = delay(millis)

/**
 * Klogging configuration that saves all log events into a mutable list, which is returned.
 *
 * @param minLevel minimum level at which to save log events
 * @return the list where log events will be saved
 */
fun savedEvents(minLevel: Level = TRACE): MutableList<LogEvent> {
    val saved: MutableList<LogEvent> = mutableListOf()
    val eventSaver: EventSender = object : EventSender {
        override fun invoke(batch: List<LogEvent>) {
            saved.addAll(batch)
        }
    }
    loggingConfiguration {
        sink("test", SinkConfiguration(eventSender = eventSaver))
        logging { fromMinLevel(minLevel) { toSink("test") } }
    }
    return saved
}
