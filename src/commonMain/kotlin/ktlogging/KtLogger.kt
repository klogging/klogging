package ktlogging

import ktlogging.events.Level
import ktlogging.events.LogEvent

/**
 * Logger interface for sending log events.
 */
interface KtLogger {

    val name: String

    fun minLevel(): Level
    fun isLevelEnabled(level: Level) = minLevel() <= level
    fun isTraceEnabled() = minLevel() <= Level.TRACE
    fun isDebugEnabled() = minLevel() <= Level.DEBUG
    fun isInfoEnabled() = minLevel() <= Level.INFO

    suspend fun logMessage(level: Level, exception: Exception?, event: Any?)

    suspend fun log(level: Level, exception: Exception, event: Any?) {
        if (isLevelEnabled(level)) logMessage(level, exception, event)
    }

    suspend fun log(level: Level, event: Any?) {
        if (isLevelEnabled(level)) logMessage(level, null, event)
    }

    suspend fun trace(event: Any?) = log(Level.TRACE, event)
    suspend fun debug(event: Any?) = log(Level.DEBUG, event)
    suspend fun info(event: Any?) = log(Level.INFO, event)
    suspend fun warn(event: Any?) = log(Level.WARN, event)
    suspend fun warn(exception: Exception, event: Any?) = log(Level.WARN, exception, event)
    suspend fun error(event: Any?) = log(Level.ERROR, event)
    suspend fun error(exception: Exception, event: Any?) = log(Level.ERROR, exception, event)
    suspend fun fatal(event: Any?) = log(Level.FATAL, event)
    suspend fun fatal(exception: Exception, event: Any?) = log(Level.FATAL, exception, event)

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
    suspend fun warn(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.WARN, exception, event)
    suspend fun error(event: suspend KtLogger.() -> Any?) = log(Level.ERROR, event)
    suspend fun error(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.ERROR, exception, event)
    suspend fun fatal(event: suspend KtLogger.() -> Any?) = log(Level.FATAL, event)
    suspend fun fatal(exception: Exception, event: suspend KtLogger.() -> Any?) = log(Level.FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    suspend fun e(template: String, vararg values: Any?): LogEvent
}