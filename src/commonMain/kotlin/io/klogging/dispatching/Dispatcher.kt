package io.klogging.dispatching

import io.klogging.config.LoggingConfiguration
import io.klogging.events.LogEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Type used for dispatching a [LogEvent] somewhere. */
public typealias DispatchEvent = (LogEvent) -> Unit

/** Simple dispatcher that sends a single, formatted line to the console. */
public val simpleDispatcher: DispatchEvent =
    { e -> println("${e.timestamp} [${e.level}] ${e.items} - ${e.logger} - ${e.message}") }

/** Object that handles dispatching of [LogEvent]s to zero or more targets. */
public object Dispatcher {

    /**
     * Dispatch a [LogEvent] to selected targets.
     *
     * Each is dispatched in a separate coroutine.
     */
    public suspend fun dispatchEvent(logEvent: LogEvent): Unit = coroutineScope {
        LoggingConfiguration
            .dispatchersFor(logEvent.logger, logEvent.level)
            .forEach { launch { it.dispatcher(logEvent) } }
    }
}
