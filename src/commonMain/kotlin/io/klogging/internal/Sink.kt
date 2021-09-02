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

package io.klogging.internal

import io.klogging.events.LogEvent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

public typealias Sender = (LogEvent) -> Unit

/**
 * Runtime management of a sink for [LogEvent]s. It contains a coroutine [Channel]
 * through which all the events for this sink pass.
 */
internal class Sink(
    internal val name: String,
    internal val sender: Sender,
) {
    private val sinkChannel: Channel<LogEvent> by lazy {
        debug("Starting sink $name")
        val channel = Channel<LogEvent>()
        CoroutineScope(Job()).launch(CoroutineName("sink-$name")) {
            for (event in channel) {
                trace("Sending event ${event.id} from sink $name")
                sender(event)
            }
        }
        channel
    }

    internal suspend fun emitEvent(event: LogEvent) {
        trace("Emitting event ${event.id} to sink $name")
        sinkChannel.send(event)
    }
}
