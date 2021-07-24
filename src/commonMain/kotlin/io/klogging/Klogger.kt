package io.klogging

import io.klogging.events.Level
import io.klogging.events.LogEvent

/**
 * Logger interface for sending log events inside coroutines.
 */
public interface Klogger : BaseLogger {

    public suspend fun logMessage(level: Level, exception: Exception?, event: Any?)

    public suspend fun log(level: Level, exception: Exception, event: Any?): Unit = logMessage(level, exception, event)
    public suspend fun log(level: Level, event: Any?): Unit = logMessage(level, null, event)

    public suspend fun log(level: Level, exception: Exception, template: String, vararg values: Any?): Unit =
        if (values.isEmpty()) logMessage(level, exception, template)
        else logMessage(level, exception, e(template, *values))

    public suspend fun log(level: Level, template: String, vararg values: Any?): Unit =
        if (values.isEmpty()) logMessage(level, null, template)
        else logMessage(level, null, e(template, *values))

    public suspend fun trace(event: Any?): Unit = log(Level.TRACE, event)
    public suspend fun debug(event: Any?): Unit = log(Level.DEBUG, event)
    public suspend fun info(event: Any?): Unit = log(Level.INFO, event)
    public suspend fun warn(event: Any?): Unit = log(Level.WARN, event)
    public suspend fun error(event: Any?): Unit = log(Level.ERROR, event)
    public suspend fun fatal(event: Any?): Unit = log(Level.FATAL, event)

    public suspend fun trace(template: String, vararg values: Any?): Unit = log(Level.TRACE, template, *values)
    public suspend fun debug(template: String, vararg values: Any?): Unit = log(Level.DEBUG, template, *values)
    public suspend fun info(template: String, vararg values: Any?): Unit = log(Level.INFO, template, *values)
    public suspend fun warn(template: String, vararg values: Any?): Unit = log(Level.WARN, template, *values)
    public suspend fun error(template: String, vararg values: Any?): Unit = log(Level.ERROR, template, *values)
    public suspend fun fatal(template: String, vararg values: Any?): Unit = log(Level.FATAL, template, *values)

    public suspend fun trace(exception: Exception, event: Any?): Unit = log(Level.WARN, exception, event)
    public suspend fun debug(exception: Exception, event: Any?): Unit = log(Level.DEBUG, exception, event)
    public suspend fun info(exception: Exception, event: Any?): Unit = log(Level.INFO, exception, event)
    public suspend fun warn(exception: Exception, event: Any?): Unit = log(Level.WARN, exception, event)
    public suspend fun error(exception: Exception, event: Any?): Unit = log(Level.ERROR, exception, event)
    public suspend fun fatal(exception: Exception, event: Any?): Unit = log(Level.FATAL, exception, event)

    public suspend fun trace(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.TRACE, exception, template, *values)

    public suspend fun debug(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.DEBUG, exception, template, *values)

    public suspend fun info(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.INFO, exception, template, *values)

    public suspend fun warn(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.WARN, exception, template, *values)

    public suspend fun error(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.ERROR, exception, template, *values)

    public suspend fun fatal(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.FATAL, exception, template, *values)

    public suspend fun log(level: Level, exception: Exception, event: suspend Klogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, exception, event())
    }

    public suspend fun log(level: Level, event: suspend Klogger.() -> Any?) {
        if (isLevelEnabled(level)) logMessage(level, null, event())
    }

    public suspend fun trace(event: suspend Klogger.() -> Any?): Unit = log(Level.TRACE, event)
    public suspend fun debug(event: suspend Klogger.() -> Any?): Unit = log(Level.DEBUG, event)
    public suspend fun info(event: suspend Klogger.() -> Any?): Unit = log(Level.INFO, event)
    public suspend fun warn(event: suspend Klogger.() -> Any?): Unit = log(Level.WARN, event)
    public suspend fun error(event: suspend Klogger.() -> Any?): Unit = log(Level.ERROR, event)
    public suspend fun fatal(event: suspend Klogger.() -> Any?): Unit = log(Level.FATAL, event)

    public suspend fun trace(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.TRACE, exception, event)

    public suspend fun debug(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.DEBUG, exception, event)

    public suspend fun info(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.INFO, exception, event)

    public suspend fun warn(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.WARN, exception, event)

    public suspend fun error(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.ERROR, exception, event)

    public suspend fun fatal(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public suspend fun e(template: String, vararg values: Any?): LogEvent
}
