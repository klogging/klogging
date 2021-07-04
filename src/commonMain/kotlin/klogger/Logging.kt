package klogger

import klogger.events.LogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/** Functional type for sending a log event to a destination. */
typealias SendEvent = (LogEvent) -> Unit

/**
 * The main object for managing log event processing.
 */
object Logging {

    /**
     * [Channel] between the coroutines where log events are sent and the coroutines that send them out.
     */
    private val logEventsChannel = startEventsChannel()

    /** Creates the channel and starts the loop to process the log events. */
    private fun startEventsChannel(): Channel<LogEvent> {
        val eventsChannel = Channel<LogEvent>()
        CoroutineScope(EmptyCoroutineContext).launch {
            for (logEvent in eventsChannel) {
                senders.forEach { it(logEvent) }
            }
        }
        return eventsChannel
    }

    /** Simple, default sender that sends formatted text to the console. */
    private val simpleSender: SendEvent =
        { e -> println("${e.timestamp} [${e.level}] ${e.items} - ${e.name} - ${e.message}") }

    /** The current list of [SendEvent] functions. */
    private val senders: MutableList<SendEvent> = mutableListOf(simpleSender)

    suspend fun dispatch(event: LogEvent) = logEventsChannel.send(event)

    fun addSender(sender: SendEvent) {
        senders.add(sender)
    }

    fun setSenders(vararg newSenders: SendEvent) {
        senders.clear()
        senders.addAll(newSenders)
    }
}
