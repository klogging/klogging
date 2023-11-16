/*

   Copyright 2021-2023 Michael Strasser.

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

import io.klogging.config.ENV_KLOGGING_BATCH_MAX_SIZE
import io.klogging.config.ENV_KLOGGING_BATCH_MAX_TIME_MS
import io.klogging.config.ENV_KLOGGING_SINK_CHANNEL_CAPACITY
import io.klogging.config.getenvInt
import io.klogging.config.getenvLong
import io.klogging.events.LogEvent
import io.klogging.sending.EventSender
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal val sinkChannelCapacity: Int = getenvInt(ENV_KLOGGING_SINK_CHANNEL_CAPACITY, 100)
internal val batchMaxTimeMs: Long = getenvLong(ENV_KLOGGING_BATCH_MAX_TIME_MS, 10)
internal val batchMaxSize: Int = getenvInt(ENV_KLOGGING_BATCH_MAX_SIZE, 100)

/**
 * Runtime management of a sink for [LogEvent]s. It contains a coroutine [Channel]
 * through which all the events for this sink pass.
 *
 * [KloggingEngine] holds a mutable map with the current [Sink]s, keyed by sink name.
 */
internal class Sink(
    internal val name: String,
    internal val eventSender: EventSender,
) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = kloggingParentContext

    private val sinkChannel: Channel<LogEvent> by lazy {
        debug("Sink", "Starting sink $name")
        val channel = Channel<LogEvent>(sinkChannelCapacity)
        launch(CoroutineName("sink-$name")) {
            // Temporary: receive events one at a time into fake batches
            // See [Issue 188](https://github.com/klogging/klogging/issues/188)
            for (logEvent in channel) {
                eventSender(listOf(logEvent))
            }
            // … instead of using the experimental coroutine API
            /*
            while (true) {
                val batch = receiveBatch(channel, batchMaxTimeMs, batchMaxSize)
                if (batch.isNotEmpty()) {
                    trace("Sink", "Sending ${batch.size} events to sink $name")
                    eventSender(batch)
                }
            }
             */
        }
        channel
    }

    /**
     * Send a [LogEvent] to the sink’s channel to be processed in batches.
     */
    internal suspend fun send(logEvent: LogEvent) {
        trace("Sink", "Forwarding event ${logEvent.id} to sink $name")
        sinkChannel.send(logEvent)
    }

    /**
     * Send a [LogEvent] directly to the sink’s [EventSender].
     */
    internal fun sendDirect(logEvent: LogEvent) {
        trace("Sink", "Forwarding event ${logEvent.id} directly to sink $name")
        eventSender(listOf(logEvent))
    }
}
