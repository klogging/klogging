package ktlogging

import ktlogging.events.Level
import ktlogging.events.LogEvent

/**
 * Interface for logging from outside coroutines.
 *
 * `NoCoLogger` is a working name for now.
 */
interface NoCoLogger : BaseLogger {

    fun logMessage(level: Level, exception: Exception?, event: Any?)

    fun log(level: Level, exception: Exception, event: Any?) {
        if (isLevelEnabled(level)) logMessage(level, exception, event)
    }

    fun log(level: Level, event: Any?) {
        if (isLevelEnabled(level)) logMessage(level, null, event)
    }

    fun trace(event: Any?) = log(Level.TRACE, event)
    fun debug(event: Any?) = log(Level.DEBUG, event)
    fun info(event: Any?) = log(Level.INFO, event)
    fun warn(event: Any?) = log(Level.WARN, event)
    fun warn(exception: Exception, event: Any?) = log(Level.WARN, exception, event)
    fun error(event: Any?) = log(Level.ERROR, event)
    fun error(exception: Exception, event: Any?) = log(Level.ERROR, exception, event)
    fun fatal(event: Any?) = log(Level.FATAL, event)
    fun fatal(exception: Exception, event: Any?) = log(Level.FATAL, exception, event)

    fun log(level: Level, exception: Exception, event: NoCoLogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, exception, event())
    }

    fun log(level: Level, event: NoCoLogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, null, event())
    }

    fun trace(event: NoCoLogger.() -> Any?) = log(Level.TRACE, event)
    fun debug(event: NoCoLogger.() -> Any?) = log(Level.DEBUG, event)
    fun info(event: NoCoLogger.() -> Any?) = log(Level.INFO, event)
    fun warn(event: NoCoLogger.() -> Any?) = log(Level.WARN, event)
    fun warn(exception: Exception, event: NoCoLogger.() -> Any?) = log(Level.WARN, exception, event)
    fun error(event: NoCoLogger.() -> Any?) = log(Level.ERROR, event)
    fun error(exception: Exception, event: NoCoLogger.() -> Any?) = log(Level.ERROR, exception, event)
    fun fatal(event: NoCoLogger.() -> Any?) = log(Level.FATAL, event)
    fun fatal(exception: Exception, event: NoCoLogger.() -> Any?) = log(Level.FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    fun e(template: String, vararg values: Any?): LogEvent

}