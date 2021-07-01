package klogger

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class Logging {
    companion object {
        internal val events: ArrayDeque<Event> = ArrayDeque(100)
        suspend fun sendEvents() {
            coroutineScope { launch { sendAllEvents() } }
        }

        private fun sendAllEvents() {
            while (events.isNotEmpty()) {
                val evt = events.removeFirst()
                eventSender(evt)
            }
        }
    }
}

typealias SendEvent = (Event) -> Unit

var eventSender: SendEvent = { e ->
    println("${e.timestamp} [${e.level}] ${e.items} ${e.name} ${e.template}")
}
