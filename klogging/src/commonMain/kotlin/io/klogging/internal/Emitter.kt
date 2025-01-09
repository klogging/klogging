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

package io.klogging.internal

import io.klogging.config.ENV_KLOGGING_EVENT_CHANNEL_CAPACITY
import io.klogging.config.getenvInt
import io.klogging.events.LogEvent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val DEFAULT_CHANNEL_CAPACITY = 100

internal val eventChannelCapacity: Int = getenvInt(ENV_KLOGGING_EVENT_CHANNEL_CAPACITY, DEFAULT_CHANNEL_CAPACITY)

/**
 * The main object for managing log event processing.
 */
internal object Emitter : CoroutineScope {

    /**
     * Context in which to launch coroutines
     */
    override val coroutineContext: CoroutineContext
        get() = kloggingParentContext

    /**
     * [Channel] between the coroutines where log events are emitted and the
     * coroutines that dispatch them to sinks.
     */
    private val logEventsChannel by lazy {
        debug("Emitter", "Starting events channel")
        val channel: Channel<LogEvent> = Channel(eventChannelCapacity)
        launch(CoroutineName("events")) {
            for (logEvent in channel) {
                trace("Emitter", "Read event ${logEvent.id} from events channel")
                Dispatcher.send(logEvent)
            }
        }
        channel
    }

    /**
     * Emit a [LogEvent] to the [logEventsChannel] to be processed asynchronously.
     * @param logEvent event to emit
     */
    suspend fun emit(logEvent: LogEvent) {
        trace("Emitter", "Emitting event ${logEvent.id} to events channel")
        logEventsChannel.send(logEvent)
    }

    /**
     * Emit a [LogEvent] directly to be processed synchronously.
     * @param logEvent event to emit directly
     */
    fun emitDirect(logEvent: LogEvent) {
        trace("Emitter", "Emitting event ${logEvent.id} directly")
        Dispatcher.sendDirect(logEvent)
    }
}
