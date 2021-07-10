package klogger

import klogger.context.LogContext
import klogger.events.Level
import klogger.events.LogEvent
import klogger.events.newId
import klogger.events.now
import kotlin.coroutines.coroutineContext

class BaseLogger(
    override val name: String,
    private val minLevel: Level = Level.INFO,
) : Klogger {

    override fun minLevel() = minLevel

    override suspend fun logMessage(level: Level, message: String) {
        val contextItems = coroutineContext[LogContext]?.getAll() ?: mapOf()
        Logging.sendEvent(
            LogEvent(
                id = newId(),
                timestamp = now(),
                name = name,
                level = level,
                message = message,
                items = contextItems
            )
        )
    }
}
