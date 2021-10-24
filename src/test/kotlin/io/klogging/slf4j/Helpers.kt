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

fun randomString() = Random.nextULong().toString(16)

suspend fun waitForDispatch(millis: Long = 200) = delay(millis)

fun savedEvents(minLevel: Level = TRACE): MutableList<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    val eventSaver: EventSender = { batch: List<LogEvent> -> saved.addAll(batch) }
    loggingConfiguration {
        sink("test", SinkConfiguration(eventSender = eventSaver))
        logging { fromMinLevel(minLevel) { toSink("test") } }
    }
    return saved
}
