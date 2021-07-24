package io.klogging

import io.klogging.dispatching.Dispatcher.dispatchEvent
import io.klogging.events.LogEvent
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
                dispatchEvent(logEvent)
            }
        }
        return eventsChannel
    }

    suspend fun sendEvent(event: LogEvent): Unit = logEventsChannel.send(event)
}
