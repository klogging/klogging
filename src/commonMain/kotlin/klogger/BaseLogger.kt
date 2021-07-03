package klogger

import klogger.context.LogContext
import kotlin.coroutines.coroutineContext

class BaseLogger(
    private val name: String,
    private val minLevel: Level = Level.INFO,
) : Klogger {

    override fun minLevel() = minLevel

    override suspend fun log(level: Level, message: String) {
        val contextItems = coroutineContext[LogContext]?.getAll()
        Logging.LOG_EVENTS.addLast(
            LogEvent(
                id = newId(),
                timestamp = now(),
                name = name,
                level = level,
                message = message,
                items = contextItems ?: mapOf()
            )
        )
        Logging.sendEvents()
    }
}
