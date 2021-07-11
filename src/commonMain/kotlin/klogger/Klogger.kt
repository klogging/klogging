package klogger

import klogger.events.Level
import klogger.events.LogEvent

/**
 * Logger interface for sending log events.
 */
interface Klogger {

    val name: String

    fun minLevel(): Level
    fun isLevelEnabled(level: Level) = minLevel() <= level
    fun isTraceEnabled() = minLevel() <= Level.TRACE
    fun isDebugEnabled() = minLevel() <= Level.DEBUG
    fun isInfoEnabled() = minLevel() <= Level.INFO

    suspend fun logMessage(level: Level, exception: Exception?, event: Any)

    suspend fun log(level: Level, exception: Exception, event: Any) {
        if (isLevelEnabled(level)) logMessage(level, exception, event)
    }

    suspend fun log(level: Level, event: Any) {
        if (isLevelEnabled(level)) logMessage(level, null, event)
    }

    suspend fun trace(event: Any) = log(Level.TRACE, event)
    suspend fun debug(event: Any) = log(Level.DEBUG, event)
    suspend fun info(event: Any) = log(Level.INFO, event)
    suspend fun warn(event: Any) = log(Level.WARN, event)
    suspend fun warn(exception: Exception, event: Any) = log(Level.WARN, exception, event)
    suspend fun error(event: Any) = log(Level.ERROR, event)
    suspend fun error(exception: Exception, event: Any) = log(Level.ERROR, exception, event)
    suspend fun fatal(event: Any) = log(Level.FATAL, event)
    suspend fun fatal(exception: Exception, event: Any) = log(Level.FATAL, exception, event)

    suspend fun log(level: Level, exception: Exception, event: Klogger.() -> Any) {
        if (isLevelEnabled(level)) logMessage(level, exception, event())
    }

    suspend fun log(level: Level, event: Klogger.() -> Any) {
        if (isLevelEnabled(level)) logMessage(level, null, event())
    }

    suspend fun trace(event: Klogger.() -> Any) = log(Level.TRACE, event)
    suspend fun debug(event: Klogger.() -> Any) = log(Level.DEBUG, event)
    suspend fun info(event: Klogger.() -> Any) = log(Level.INFO, event)
    suspend fun warn(event: Klogger.() -> Any) = log(Level.WARN, event)
    suspend fun warn(exception: Exception, event: Klogger.() -> Any) = log(Level.WARN, exception, event)
    suspend fun error(event: Klogger.() -> Any) = log(Level.ERROR, event)
    suspend fun error(exception: Exception, event: Klogger.() -> Any) = log(Level.ERROR, exception, event)
    suspend fun fatal(event: Klogger.() -> Any) = log(Level.FATAL, event)
    suspend fun fatal(exception: Exception, event: Klogger.() -> Any) = log(Level.FATAL, exception, event)

    suspend fun e(template: String, vararg items: Any): LogEvent
}