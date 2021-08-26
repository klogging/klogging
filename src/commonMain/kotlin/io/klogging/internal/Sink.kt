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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope

public typealias Sender = (LogEvent) -> Unit

/**
 * 
 */
internal class Sink(
    private val name: String,
    private val sender: Sender,
) {
    private val sinkChannel: Channel<LogEvent> = Channel()
    
    internal suspend fun emitEvent(event: LogEvent) = sinkChannel.send(event)
        
    internal suspend fun start() = coroutineScope {
        debug("starting sink $name")
        launch {
            for (event in sinkChannel) {
                debug("sending event ${event.id} to sink $name")
                sender(event)
            }
        }
    }
    
    internal suspend fun stop() {
        debug("stopping sink $name")
        sinkChannel.close()
    }
}

