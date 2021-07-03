package klogger

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class Logging {
    companion object {
        internal val LOG_EVENTS: ArrayDeque<LogEvent> = ArrayDeque(100)
        suspend fun sendEvents() {
            coroutineScope { launch { sendAllEvents() } }
        }

        private fun sendAllEvents() {
            while (LOG_EVENTS.isNotEmpty()) {
                val evt = LOG_EVENTS.removeFirst()
                eventSender(evt)
            }
        }
    }
}

typealias SendEvent = (LogEvent) -> Unit

var eventSender: SendEvent = { e ->
    println("${e.timestamp} [${e.level}] ${e.items} ${e.name} ${e.message}")
}
