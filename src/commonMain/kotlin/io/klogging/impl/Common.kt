package io.klogging.impl

import io.klogging.BaseLogger
import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.events.now

public fun LogEvent.copyWith(newLevel: Level, newStacktrace: String?): LogEvent = LogEvent(
    timestamp, host, logger, newLevel, template, message, newStacktrace, items
)

public fun BaseLogger.eventFrom(
    level: Level,
    exception: Exception?,
    event: Any?,
    withItems: Map<String, Any?> = mapOf(),
): LogEvent {
    return when (event) {
        is LogEvent ->
            event.copyWith(level, exception?.stackTraceToString())
        else -> {
            val (message, stackTrace) = messageAndStackTrace(event, exception)
            LogEvent(
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
