package io.klogging.impl

import io.klogging.Logging
import io.klogging.context.LogContext
import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.events.now
import io.klogging.template.templateItems
import kotlin.coroutines.coroutineContext

public class KloggerImpl(
    override val name: String,
) : io.klogging.Klogger {

    override suspend fun logMessage(level: Level, exception: Exception?, event: Any?) {
        val eventToLog = eventFrom(level, exception, event, contextItems())
        Logging.sendEvent(eventToLog)
    }

    private suspend inline fun contextItems() =
        coroutineContext[LogContext]?.getAll() ?: mapOf()

    override suspend fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values).mapValues { e -> e.value.toString() }
        return LogEvent(
            timestamp = now(),
            logger = this.name,
            level = minLevel(),
            template = template,
            message = template,
            stackTrace = null,
            items = items + contextItems(),
        )
    }
}
