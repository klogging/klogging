package io.klogging

import io.klogging.events.Level
import io.klogging.events.LogEvent

/**
 * Interface for logging from outside coroutines.
 *
 * `NoCoLogger` is a working name for now.
 */
public interface NoCoLogger : BaseLogger {

    public fun logMessage(level: Level, exception: Exception?, event: Any?)

    public fun log(level: Level, exception: Exception, event: Any?): Unit = logMessage(level, exception, event)
    public fun log(level: Level, event: Any?): Unit = logMessage(level, null, event)

    public fun log(level: Level, exception: Exception, template: String, vararg values: Any?): Unit =
        if (values.isEmpty()) logMessage(level, exception, template)
        else logMessage(level, exception, e(template, *values))

    public fun log(level: Level, template: String, vararg values: Any?): Unit =
        if (values.isEmpty()) logMessage(level, null, template)
        else logMessage(level, null, e(template, *values))

    public fun trace(event: Any?): Unit = log(Level.TRACE, event)
    public fun debug(event: Any?): Unit = log(Level.DEBUG, event)
    public fun info(event: Any?): Unit = log(Level.INFO, event)
    public fun warn(event: Any?): Unit = log(Level.WARN, event)
    public fun error(event: Any?): Unit = log(Level.ERROR, event)
    public fun fatal(event: Any?): Unit = log(Level.FATAL, event)

    public fun trace(template: String, vararg values: Any?): Unit = log(Level.TRACE, template, *values)
    public fun debug(template: String, vararg values: Any?): Unit = log(Level.DEBUG, template, *values)
    public fun info(template: String, vararg values: Any?): Unit = log(Level.INFO, template, *values)
    public fun warn(template: String, vararg values: Any?): Unit = log(Level.WARN, template, *values)
    public fun error(template: String, vararg values: Any?): Unit = log(Level.ERROR, template, *values)
    public fun fatal(template: String, vararg values: Any?): Unit = log(Level.FATAL, template, *values)

    public fun trace(exception: Exception, event: Any?): Unit = log(Level.TRACE, exception, event)
    public fun debug(exception: Exception, event: Any?): Unit = log(Level.DEBUG, exception, event)
    public fun info(exception: Exception, event: Any?): Unit = log(Level.INFO, exception, event)
    public fun warn(exception: Exception, event: Any?): Unit = log(Level.WARN, exception, event)
    public fun error(exception: Exception, event: Any?): Unit = log(Level.ERROR, exception, event)
    public fun fatal(exception: Exception, event: Any?): Unit = log(Level.FATAL, exception, event)

    public fun trace(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.TRACE, exception, template, *values)

    public fun debug(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.DEBUG, exception, template, *values)

    public fun info(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.INFO, exception, template, *values)

    public fun warn(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.WARN, exception, template, *values)

    public fun error(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.ERROR, exception, template, *values)

    public fun fatal(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.FATAL, exception, template, *values)

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
    public fun error(event: NoCoLogger.() -> Any?): Unit = log(Level.ERROR, event)
    public fun fatal(event: NoCoLogger.() -> Any?): Unit = log(Level.FATAL, event)

    public fun trace(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.TRACE, exception, event)
    public fun debug(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.DEBUG, exception, event)
    public fun info(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.INFO, exception, event)
    public fun warn(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.WARN, exception, event)
    public fun error(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.ERROR, exception, event)
    public fun fatal(exception: Exception, event: NoCoLogger.() -> Any?): Unit = log(Level.FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public fun e(template: String, vararg values: Any?): LogEvent
}
