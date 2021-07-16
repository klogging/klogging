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
        val eventToLog = when (event) {
            is LogEvent -> event.copyWith(level, exception?.stackTraceToString())
            else -> {
                val (message, stackTrace) = when (event) {
                    is Exception -> (event.message ?: "Exception") to event.stackTraceToString()
                    else -> event.toString() to exception?.stackTraceToString()
                }
                LogEvent(
                    id = newId(),
                    timestamp = now(),
                    logger = name,
                    level = level,
                    message = message,
                    stackTrace = stackTrace,
                    items = mapOf()
                )
            }
        }

        CoroutineScope(Job()).launch {
            Logging.sendEvent(eventToLog)
        }
    }

    override fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values).mapValues { v -> v.toString() }
        return LogEvent(
            id = newId(),
            timestamp = now(),
            logger = name,
            level = minLevel,
            template = template,
            message = template,
            stackTrace = null,
            items = items,
        )
    }
}