package ktlogging

import ktlogging.config.LoggingConfiguration
import ktlogging.events.Level

public interface BaseLogger {

    public val name: String

    public fun minLevel(): Level = LoggingConfiguration.minimumLevelOf(name)

    public fun isLevelEnabled(level: Level): Boolean = minLevel() <= level
    public fun isTraceEnabled(): Boolean = minLevel() <= Level.TRACE
    public fun isDebugEnabled(): Boolean = minLevel() <= Level.DEBUG
    public fun isInfoEnabled(): Boolean = minLevel() <= Level.INFO
    public fun isWarnEnabled(): Boolean = minLevel() <= Level.WARN
    public fun isErrorEnabled(): Boolean = minLevel() <= Level.ERROR
    public fun isFatalEnabled(): Boolean = minLevel() <= Level.FATAL
}
