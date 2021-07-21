package ktlogging

import ktlogging.config.LoggingConfiguration
import ktlogging.events.Level

interface BaseLogger {

    val name: String

    fun minLevel(): Level = LoggingConfiguration.minimumLevelOf(name)

    fun isLevelEnabled(level: Level) = minLevel() <= level
    fun isTraceEnabled() = minLevel() <= Level.TRACE
    fun isDebugEnabled() = minLevel() <= Level.DEBUG
    fun isInfoEnabled() = minLevel() <= Level.INFO
    fun isWarnEnabled() = minLevel() <= Level.WARN
    fun isErrorEnabled() = minLevel() <= Level.ERROR
    fun isFatalEnabled() = minLevel() <= Level.FATAL
}
