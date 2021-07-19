package ktlogging.config

import ktlogging.dispatching.DispatchEvent
import ktlogging.dispatching.simpleDispatcher
import ktlogging.events.Level

data class LogDispatcher(
    val name: String,
    val dispatcher: DispatchEvent,
)

data class LoggingConfig(
    val name: String,
    val level: Level,
    val dispatchers: List<LogDispatcher>,
)

const val ROOT_CONFIG = "ROOT"
val DEFAULT_CONSOLE = LoggingConfig(ROOT_CONFIG, Level.INFO, listOf(LogDispatcher("CONSOLE", simpleDispatcher)))

object LoggingConfiguration {

    private val configs: MutableList<LoggingConfig> = mutableListOf(DEFAULT_CONSOLE)

    fun setConfigs(vararg newConfigs: LoggingConfig) {
        configs.clear()
        configs.addAll(newConfigs)
    }

    fun dispatchersFor(name: String, level: Level): List<LogDispatcher> = configs
        .filter { matchesName(it, name) && minLevel(it, level) }
        .flatMap { it.dispatchers }

    private inline fun matchesName(config: LoggingConfig, name: String) =
        config.name == ROOT_CONFIG || name.startsWith(config.name)

    private inline fun minLevel(config: LoggingConfig, level: Level) = level >= config.level

    fun minimumLevelOf(name: String): Level = configs
        .filter { matchesName(it, name) }
        .minOfOrNull { it.level } ?: Level.NONE
}
