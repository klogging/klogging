package klogger

/**
 * Logger interface for sending log events.
 */
interface Klogger {

    fun minLevel(): Level
    fun isLevelEnabled(level: Level) = minLevel() <= level
    fun isTraceEnabled() = minLevel() <= Level.TRACE
    fun isDebugEnabled() = minLevel() <= Level.DEBUG
    fun isInfoEnabled() = minLevel() <= Level.INFO

    fun log(level: Level, message: String)
    fun trace(message: String) = log(Level.TRACE, message)
    fun debug(message: String) = log(Level.DEBUG, message)
    fun info(message: String) = log(Level.INFO, message)
    fun warn(message: String) = log(Level.WARN, message)
    fun error(message: String) = log(Level.ERROR, message)
    fun fatal(message: String) = log(Level.FATAL, message)

    fun log(level: Level, message: () -> String) {
        if (isLevelEnabled(level)) log(level, message())
    }

    fun trace(message: () -> String) = log(Level.TRACE, message)
    fun debug(message: () -> String) = log(Level.DEBUG, message)
    fun info(message: () -> String) = log(Level.INFO, message)
    fun warn(message: () -> String) = log(Level.WARN, message)
    fun error(message: () -> String) = log(Level.ERROR, message)
    fun fatal(message: () -> String) = log(Level.FATAL, message)
}