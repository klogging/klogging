package ktlogging

import ktlogging.events.Level
import ktlogging.events.LogEvent

/**
 * Interface for logging from outside coroutines.
 *
 * `NoCoLogger` is a working name for now.
 */
public interface NoCoLogger : BaseLogger {

    public fun logMessage(level: Level, exception: Exception?, event: Any?)

    public fun log(level: Level, exception: Exception, event: Any?) {
        if (isLevelEnabled(level)) logMessage(level, exception, event)
    }

    public fun log(level: Level, event: Any?) {
        if (isLevelEnabled(level)) logMessage(level, null, event)
    }

    public fun trace(event: Any?): Unit = log(Level.TRACE, event)
    public fun debug(event: Any?): Unit = log(Level.DEBUG, event)
    public fun info(event: Any?): Unit = log(Level.INFO, event)
    public fun warn(event: Any?): Unit = log(Level.WARN, event)
    public fun warn(exception: Exception, event: Any?): Unit = log(Level.WARN, exception, event)
    public fun error(event: Any?): Unit = log(Level.ERROR, event)
    public fun error(exception: Exception, event: Any?): Unit = log(Level.ERROR, exception, event)
    public fun fatal(event: Any?): Unit = log(Level.FATAL, event)
    public fun fatal(exception: Exception, event: Any?): Unit = log(Level.FATAL, exception, event)

    public fun log(level: Level, exception: Exception, event: NoCoLogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, exception, event())
    }

    public fun log(level: Level, event: NoCoLogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, null, event())
    }

    public fun trace(event: NoCoLogger.() -> Any?): Unit = log(Level.TRACE, event)
    public fun debug(event: NoCoLogger.() -> Any?): Unit = log(Level.DEBUG, event)
    public fun info(event: NoCoLogger.() -> Any?): Unit = log(Level.INFO, event)
    public fun warn(event: NoCoLogger.() -> Any?): Unit = log(Level.WARN, event)
    public fun warn(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.WARN, exception, event)
    public fun error(event: NoCoLogger.() -> Any?): Unit = log(Level.ERROR, event)
    public fun error(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.ERROR, exception, event)
    public fun fatal(event: NoCoLogger.() -> Any?): Unit = log(Level.FATAL, event)
    public fun fatal(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public fun e(template: String, vararg values: Any?): LogEvent
}
