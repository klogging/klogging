package klogger

import klogger.events.Level

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

    suspend fun log(level: Level, message: String) {
        if (isLevelEnabled(level)) logMessage(level, message)
    }

    suspend fun trace(message: String) = log(Level.TRACE, message)
    suspend fun debug(message: String) = log(Level.DEBUG, message)
    suspend fun info(message: String) = log(Level.INFO, message)
    suspend fun warn(message: String) = log(Level.WARN, message)
    suspend fun error(message: String) = log(Level.ERROR, message)
    suspend fun fatal(message: String) = log(Level.FATAL, message)

    suspend fun logMessage(level: Level, message: String)

    suspend fun log(level: Level, message: () -> String) {
        if (isLevelEnabled(level)) logMessage(level, message())
    }

    suspend fun trace(message: () -> String) = log(Level.TRACE, message)
    suspend fun debug(message: () -> String) = log(Level.DEBUG, message)
    suspend fun info(message: () -> String) = log(Level.INFO, message)
    suspend fun warn(message: () -> String) = log(Level.WARN, message)
    suspend fun error(message: () -> String) = log(Level.ERROR, message)
    suspend fun fatal(message: () -> String) = log(Level.FATAL, message)
}