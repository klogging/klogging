package io.klogging.impl

import io.klogging.Logging
import io.klogging.NoCoLogger
import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.events.now
import io.klogging.template.templateItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

public class NoCoLoggerImpl(
    override val name: String,
) : NoCoLogger {

    override fun logMessage(level: Level, exception: Exception?, event: Any?) {
        val eventToLog = eventFrom(level, exception, event)
        CoroutineScope(Job()).launch {
            Logging.sendEvent(eventToLog)
        }
    }

    override fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values).mapValues { e -> e.value.toString() }
        return LogEvent(
            timestamp = now(),
            logger = this.name,
            level = minLevel(),
            template = template,
            message = template,
            stackTrace = null,
            items = items,
        )
    }
}
