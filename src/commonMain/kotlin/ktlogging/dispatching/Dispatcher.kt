package ktlogging.dispatching

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ktlogging.config.LoggingConfiguration
import ktlogging.events.LogEvent

/** Type used for dispatching a [LogEvent] somewhere. */
typealias DispatchEvent = (LogEvent) -> Unit

/** Simple dispatcher that sends a single, formatted line to the console. */
val simpleDispatcher: DispatchEvent =
    { e -> println("${e.timestamp} [${e.level}] ${e.items} - ${e.logger} - ${e.message}") }

/** Object that handles dispatching of [LogEvent]s to zero or more targets. */
object Dispatcher {

    /**
     * Dispatch a [LogEvent] to selected targets.
     *
     * Each is dispatched in a separate coroutine.
     */
    suspend fun dispatchEvent(logEvent: LogEvent) = coroutineScope {
        LoggingConfiguration
            .dispatchersFor(logEvent.logger, logEvent.level)
            .forEach { launch { it.dispatcher(logEvent) } }
    }
}
