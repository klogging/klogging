package klogger

import klogger.Dispatcher.dispatchEvent
import klogger.events.LogEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * The main object for managing log event processing.
 */
@DelicateCoroutinesApi
object Logging {
    /**
     * [Channel] between the coroutines where log events are sent and the coroutines that send them out.
     */
    private val logEventsChannel = startEventsChannel()

    /** Creates the channel and starts the loop to process the log events. */
    private fun startEventsChannel(): Channel<LogEvent> {
        val eventsChannel = Channel<LogEvent>()
        // Use GlobalScope because the channel should exist as long as the runtime is active.
        GlobalScope.launch {
            for (logEvent in eventsChannel) {
                dispatchEvent(logEvent)
            }
        }
        return eventsChannel
    }

    suspend fun sendEvent(event: LogEvent) = logEventsChannel.send(event)
}
