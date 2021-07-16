package ktlogging

import ktlogging.events.Level

interface BaseLogger {

    val name: String

    fun minLevel(): Level
    fun isLevelEnabled(level: Level) = minLevel() <= level
    fun isTraceEnabled() = minLevel() <= Level.TRACE
    fun isDebugEnabled() = minLevel() <= Level.DEBUG
    fun isInfoEnabled() = minLevel() <= Level.INFO
}