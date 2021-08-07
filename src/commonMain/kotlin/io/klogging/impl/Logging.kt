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

package io.klogging.impl

import io.klogging.config.KLOGGING_LOGGER
import io.klogging.dispatching.Dispatcher.dispatchEvent
import io.klogging.events.LogEvent
import io.klogging.internal.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * The main object for managing log event processing.
 */
internal object Logging {

    /**
     * [Channel] between the coroutines where log events are sent and the coroutines that send them out.
     */
    private val logEventsChannel = startEventsChannel()

    /** Creates the channel and starts the loop to process the log events. */
    private fun startEventsChannel(): Channel<LogEvent> {
        val eventsChannel = Channel<LogEvent>()
        CoroutineScope(Job()).launch {
            for (logEvent in eventsChannel) {
                debug(KLOGGING_LOGGER, "Read event ${logEvent.id}")
                dispatchEvent(logEvent)
            }
        }
        return eventsChannel
    }

    suspend fun sendEvent(logEvent: LogEvent) {
        debug(KLOGGING_LOGGER, "Sending event with ${logEvent.id}")
        logEventsChannel.send(logEvent)
    }
}
