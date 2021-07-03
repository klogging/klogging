package klogger

import klogger.events.LogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class Logging {
    companion object {
        private val logEvents = Channel<LogEvent>()

        private val dispatcher = EventDispatcher(logEvents).start()

        suspend fun dispatch(event: LogEvent) = logEvents.send(event)
    }
}

class EventDispatcher(private val channel: ReceiveChannel<LogEvent>) {
    fun start(): String {
        CoroutineScope(EmptyCoroutineContext).launch {
            for (logEvent in channel) {
                eventSender(logEvent)
            }
        }
        return "Started"
    }
}

typealias SendEvent = (LogEvent) -> Unit

var eventSender: SendEvent = { e ->
    println("${e.timestamp} [${e.level}] ${e.items} ${e.name} ${e.message}")
}
