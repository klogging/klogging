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

    override suspend fun logMessage(level: Level, event: Any) {
        val contextItems = coroutineContext[LogContext]?.getAll() ?: mapOf()
        when (event) {
            is String ->
                Logging.sendEvent(
                    LogEvent(
                        id = newId(),
                        timestamp = now(),
                        name = name,
                        level = level,
                        message = event,
                        items = contextItems
                    )
                )
            is LogEvent ->
                Logging.sendEvent(event)
        }
    }

}
