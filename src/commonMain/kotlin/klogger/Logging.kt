package klogger

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias SendEvent = (Event) -> Unit

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

var eventSender: SendEvent = { evt ->
    println("${evt.timestamp} [${evt.level}] ${evt.items} ${evt.template}")
}
