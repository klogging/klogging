package klogger

import klogger.events.LogEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

typealias DispatchEvent = (LogEvent) -> Unit

object Dispatcher {

    private val simpleDispatcher: DispatchEvent =
        { e -> println("${e.timestamp} [${e.level}] ${e.items} - ${e.name} - ${e.message}") }

    private val dispatchers = mutableListOf(simpleDispatcher)

    suspend fun dispatchEvent(logEvent: LogEvent) = coroutineScope {
        dispatchers.forEach { launch { it(logEvent) } }
    }

    fun addDispatcher(sender: DispatchEvent) {
        dispatchers.add(sender)
    }

    fun setDispatchers(vararg newSenders: DispatchEvent) {
        dispatchers.clear()
        dispatchers.addAll(newSenders)
    }
}
