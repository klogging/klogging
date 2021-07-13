package ktlogging

import ktlogging.events.LogEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Type used for dispatching a [LogEvent] somewhere. */
typealias DispatchEvent = (LogEvent) -> Unit

/** Object that handles dispatching of [LogEvent]s to zero or more destinations. */
object Dispatcher {

    /** Simple dispatcher that sends a single, formatted line to the console. */
    private val simpleDispatcher: DispatchEvent =
        { e -> println("${e.timestamp} [${e.level}] ${e.items} - ${e.logger} - ${e.message}") }

    /** List of dispatchers that can be changed at any time. */
    private val dispatchers: MutableList<DispatchEvent> = mutableListOf(simpleDispatcher)

    /**
     * Dispatch a [LogEvent] to all current destinations.
     *
     * Each is dispatched in a separate coroutine.
     */
    suspend fun dispatchEvent(logEvent: LogEvent) = coroutineScope {
        dispatchers.forEach { launch { it(logEvent) } }
    }

    /** Adds a dispatcher to the list. */
    fun addDispatcher(dispatcher: DispatchEvent) {
        dispatchers.add(dispatcher)
    }

    /** Replaces current dispatchers with zero or more others. */
    fun setDispatchers(vararg newDispatchers: DispatchEvent) {
        dispatchers.clear()
        dispatchers.addAll(newDispatchers)
    }
}
