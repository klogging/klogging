package klogger

import klogger.events.LogEvent

typealias DispatchEvent = (LogEvent) -> Unit

object Dispatcher {

    private val simpleDispatcher: DispatchEvent =
        { e -> println("${e.timestamp} [${e.level}] ${e.items} - ${e.name} - ${e.message}") }

    private val dispatchers = mutableListOf(simpleDispatcher)

    fun dispatchEvent(logEvent: LogEvent) {
        dispatchers.forEach { it(logEvent) }
    }

    fun addDispatcher(sender: DispatchEvent) {
        dispatchers.add(sender)
    }

    fun setDispatchers(vararg newSenders: DispatchEvent) {
        dispatchers.clear()
        dispatchers.addAll(newSenders)
    }
}
