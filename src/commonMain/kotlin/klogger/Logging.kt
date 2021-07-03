package klogger

import klogger.Logging.Companion.senders
import klogger.events.LogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

typealias SendEvent = (LogEvent) -> Unit

var SIMPLE_SENDER: SendEvent = { e ->
    println("${e.timestamp} [${e.level}] ${e.items} ${e.name} ${e.message}")
}

class Logging {
    companion object {
        private val logEvents = Channel<LogEvent>()

        private val dispatcher = EventDispatcher(logEvents).start()

        suspend fun dispatch(event: LogEvent) = logEvents.send(event)

        internal val senders: MutableList<SendEvent> = mutableListOf(SIMPLE_SENDER)

        fun addSender(sender: SendEvent) { senders.add(sender) }

        fun setSenders(vararg newSenders: SendEvent) {
            senders.clear()
            senders.addAll(newSenders)
        }
    }
}

class EventDispatcher(private val channel: ReceiveChannel<LogEvent>) {
    fun start(): String {
        CoroutineScope(EmptyCoroutineContext).launch {
            for (logEvent in channel) {
                senders.forEach { it(logEvent) }
            }
        }
        return "Started"
    }
}
