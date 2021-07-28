/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.config

import io.klogging.dispatching.DispatchEvent
import io.klogging.dispatching.simpleDispatcher
import io.klogging.events.Level

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
