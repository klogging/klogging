package ktlogging.config

import ktlogging.dispatching.DispatchEvent
import ktlogging.dispatching.simpleDispatcher
import ktlogging.events.Level

public data class LogDispatcher(
    val name: String,
    val dispatcher: DispatchEvent,
)

public data class LoggingConfig(
    val name: String,
    val level: Level,
    val dispatchers: List<LogDispatcher>,
)

public const val ROOT_CONFIG: String = "ROOT"
public val DEFAULT_CONSOLE: LoggingConfig = LoggingConfig(ROOT_CONFIG, Level.INFO, listOf(LogDispatcher("CONSOLE", simpleDispatcher)))

public object LoggingConfiguration {

    private val configs: MutableList<LoggingConfig> = mutableListOf(DEFAULT_CONSOLE)

    public fun setConfigs(vararg newConfigs: LoggingConfig) {
        configs.clear()
        configs.addAll(newConfigs)
    }

    public fun dispatchersFor(name: String, level: Level): List<LogDispatcher> = configs
        .filter { matchesName(it, name) && minLevel(it, level) }
        .flatMap { it.dispatchers }

    private fun matchesName(config: LoggingConfig, name: String) =
        config.name == ROOT_CONFIG || name.startsWith(config.name)

    private fun minLevel(config: LoggingConfig, level: Level) = level >= config.level

    internal fun minimumLevelOf(name: String): Level = configs
        .filter { matchesName(it, name) }
        .minOfOrNull { it.level } ?: Level.NONE
}
