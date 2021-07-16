package ktlogging.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ktlogging.Logging
import ktlogging.NoCoLogger
import ktlogging.events.Level
import ktlogging.events.LogEvent
import ktlogging.events.newId
import ktlogging.events.now
import ktlogging.template.templateItems

class NoCoLoggerImpl(
    override val name: String,
    private val minLevel: Level = Level.INFO,
) : NoCoLogger {

    override fun minLevel() = minLevel

    override fun logMessage(level: Level, exception: Exception?, event: Any?) {
        val eventToLog = eventFrom(level, exception, event)
        CoroutineScope(Job()).launch {
            Logging.sendEvent(eventToLog)
        }
    }

    override fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values).mapValues { e -> e.value.toString() }
        return LogEvent(
            id = newId(),
            timestamp = now(),
            logger = this.name,
            level = this.minLevel,
            template = template,
            message = template,
            stackTrace = null,
            items = items,
        )
    }
}