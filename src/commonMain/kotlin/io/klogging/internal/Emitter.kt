/*

   Copyright 2022 Michael Strasser.

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

package io.klogging.internal

import io.klogging.config.ENV_KLOGGING_EVENT_CHANNEL_CAPACITY
import io.klogging.config.getenvInt
import io.klogging.events.LogEvent
import io.klogging.internal.Dispatcher.dispatchEvent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal val eventChannelCapacity: Int = getenvInt(ENV_KLOGGING_EVENT_CHANNEL_CAPACITY, 100)

/**
 * The main object for managing log event processing.
 */
internal object Emitter : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = kloggingParentContext

    /**
     * [Channel] between the coroutines where log events are emitted and the
     * coroutines that dispatch them to sinks.
     */
    private val logEventsChannel by lazy {
        debug("Emitter", "Starting events channel")
        val channel = Channel<LogEvent>(eventChannelCapacity)
        launch(CoroutineName("events")) {
            for (logEvent in channel) {
                trace("Emitter", "Read event ${logEvent.id} from events channel")
                dispatchEvent(logEvent)
            }
        }
        channel
    }

    suspend fun sendEvent(logEvent: LogEvent) {
        trace("Emitter", "Emitting event ${logEvent.id} to events channel")
        logEventsChannel.send(logEvent)
    }
}
