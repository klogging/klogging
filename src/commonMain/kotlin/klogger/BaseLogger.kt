package klogger

import klogger.context.LogContext
import kotlin.coroutines.coroutineContext

class BaseLogger(
    private val name: String,
) : Klogger {

    // Starting point for now.
    override fun minLevel(): Level = Level.INFO

    override suspend fun log(level: Level, message: String) {
        val contextItems = coroutineContext[LogContext]?.getAll()
        Logging.events.addLast(
            Event(
                id = newId(),
                timestamp = now(),
                name = name,
                level = level,
                template = message,
                marker = null,
                items = contextItems ?: mapOf()
            )
        )
        Logging.sendEvents()
    }
}