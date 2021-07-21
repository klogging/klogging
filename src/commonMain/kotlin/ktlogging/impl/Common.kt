package ktlogging.impl

import ktlogging.BaseLogger
import ktlogging.events.Level
import ktlogging.events.LogEvent
import ktlogging.events.newId
import ktlogging.events.now

fun LogEvent.copyWith(newLevel: Level, newStacktrace: String?) = LogEvent(
    id, timestamp, host, logger, newLevel, template, message, newStacktrace, items
)

fun BaseLogger.eventFrom(
    level: Level,
    exception: Exception?,
    event: Any?,
    withItems: Map<String, String> = mapOf(),
): LogEvent {
    return when (event) {
        is LogEvent ->
            event.copyWith(level, exception?.stackTraceToString())
        else -> {
            val (message, stackTrace) = messageAndStackTrace(event, exception)
            LogEvent(
                id = newId(),
                timestamp = now(),
                logger = this.name,
                level = level,
                message = message,
                stackTrace = stackTrace,
                items = withItems,
            )
        }
    }
}

internal fun messageAndStackTrace(event: Any?, exception: Exception?) = when (event) {
    is Exception -> (event.message ?: "Exception") to event.stackTraceToString()
    else -> event.toString() to exception?.stackTraceToString()
}
