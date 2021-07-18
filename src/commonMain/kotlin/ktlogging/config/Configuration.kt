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

val DEFAULT_CONSOLE = LoggingConfig("ROOT", Level.INFO, listOf(LogDispatcher("CONSOLE", simpleDispatcher)))

object LoggingConfiguration {

    private val configs: MutableList<LoggingConfig> = mutableListOf(DEFAULT_CONSOLE)

    fun setConfigs(vararg newConfigs: LoggingConfig) {
        configs.clear()
        configs.addAll(newConfigs)
    }

    fun dispatchersFor(name: String, level: Level): List<LogDispatcher> {
        return configs.flatMap { it.dispatchers }
    }
}
