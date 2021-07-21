package ktlogging

import ktlogging.events.Level
import ktlogging.events.LogEvent

/**
 * Logger interface for sending log events inside coroutines.
 */
interface KtLogger : BaseLogger {

    suspend fun logMessage(level: Level, exception: Exception?, event: Any?)

    suspend fun log(level: Level, exception: Exception, event: Any?) = logMessage(level, exception, event)
    suspend fun log(level: Level, event: Any?) = logMessage(level, null, event)

    suspend fun log(level: Level, exception: Exception, template: String, vararg values: Any?) =
        if (values.isEmpty()) logMessage(level, exception, template)
        else logMessage(level, exception, e(template, *values))

    suspend fun log(level: Level, template: String, vararg values: Any?) =
        if (values.isEmpty()) logMessage(level, null, template)
        else logMessage(level, null, e(template, *values))

    suspend fun trace(event: Any?) = log(Level.TRACE, event)
    suspend fun debug(event: Any?) = log(Level.DEBUG, event)
    suspend fun info(event: Any?) = log(Level.INFO, event)
    suspend fun warn(event: Any?) = log(Level.WARN, event)
    suspend fun error(event: Any?) = log(Level.ERROR, event)
    suspend fun fatal(event: Any?) = log(Level.FATAL, event)

    suspend fun trace(template: String, vararg values: Any?) = log(Level.TRACE, template, *values)
    suspend fun debug(template: String, vararg values: Any?) = log(Level.DEBUG, template, *values)
    suspend fun info(template: String, vararg values: Any?) = log(Level.INFO, template, *values)
    suspend fun warn(template: String, vararg values: Any?) = log(Level.WARN, template, *values)
    suspend fun error(template: String, vararg values: Any?) = log(Level.ERROR, template, *values)
    suspend fun fatal(template: String, vararg values: Any?) = log(Level.FATAL, template, *values)

    suspend fun trace(exception: Exception, event: Any?) = log(Level.WARN, exception, event)
    suspend fun debug(exception: Exception, event: Any?) = log(Level.DEBUG, exception, event)
    suspend fun info(exception: Exception, event: Any?) = log(Level.INFO, exception, event)
    suspend fun warn(exception: Exception, event: Any?) = log(Level.WARN, exception, event)
    suspend fun error(exception: Exception, event: Any?) = log(Level.ERROR, exception, event)
    suspend fun fatal(exception: Exception, event: Any?) = log(Level.FATAL, exception, event)

    suspend fun trace(exception: Exception, template: String, vararg values: Any?) =
        log(Level.TRACE, exception, template, *values)

    suspend fun debug(exception: Exception, template: String, vararg values: Any?) =
        log(Level.DEBUG, exception, template, *values)

    suspend fun info(exception: Exception, template: String, vararg values: Any?) =
        log(Level.INFO, exception, template, *values)

    suspend fun warn(exception: Exception, template: String, vararg values: Any?) =
        log(Level.WARN, exception, template, *values)

    suspend fun error(exception: Exception, template: String, vararg values: Any?) =
        log(Level.ERROR, exception, template, *values)

    suspend fun fatal(exception: Exception, template: String, vararg values: Any?) =
        log(Level.FATAL, exception, template, *values)

    suspend fun log(level: Level, exception: Exception, event: suspend KtLogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, exception, event())
    }

    suspend fun log(level: Level, event: suspend KtLogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, null, event())
    }

    suspend fun trace(event: suspend KtLogger.() -> Any?) = log(Level.TRACE, event)
    suspend fun debug(event: suspend KtLogger.() -> Any?) = log(Level.DEBUG, event)
    suspend fun info(event: suspend KtLogger.() -> Any?) = log(Level.INFO, event)
    suspend fun warn(event: suspend KtLogger.() -> Any?) = log(Level.WARN, event)
    suspend fun error(event: suspend KtLogger.() -> Any?) = log(Level.ERROR, event)
    suspend fun fatal(event: suspend KtLogger.() -> Any?) = log(Level.FATAL, event)

    suspend fun trace(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.TRACE, exception, event)
    suspend fun debug(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.DEBUG, exception, event)
    suspend fun info(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.INFO, exception, event)
    suspend fun warn(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.WARN, exception, event)
    suspend fun error(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.ERROR, exception, event)
    suspend fun fatal(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    suspend fun e(template: String, vararg values: Any?): LogEvent
}
