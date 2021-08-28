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
import io.klogging.internal.debug
import io.klogging.internal.trace
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

public typealias Sender = (LogEvent) -> Unit

/**
 * 
 */
internal class Sink(
    private val name: String,
    private val sender: Sender,
) {
    private val sinkChannel: Channel<LogEvent> by lazy {
        debug("Starting sink $name")
        val channel = Channel<LogEvent>()
        CoroutineScope(Job()).launch(CoroutineName("sink-$name")) {
            for (event in channel) {
                trace("Sending event ${event.id} to sink $name")
                sender(event)
            }
        }
        channel
    }
    
    internal suspend fun emitEvent(event: LogEvent) = sinkChannel.send(event)

    internal suspend fun stop() {
        debug("Stopping sink $name")
        sinkChannel.close()
    }
}

