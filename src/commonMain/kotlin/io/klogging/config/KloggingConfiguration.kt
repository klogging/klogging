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

import io.klogging.dispatching.DispatchString
import io.klogging.events.Level
import io.klogging.render.RenderString

/**
 * Klogging configuration for a runtime.
 */
public object KloggingConfiguration {

    internal val sinks = mutableMapOf<String, SinkConfiguration>()
    internal val configs = mutableListOf<LoggingConfig>()

    /** DSL: add a sink configuration. */
    public fun sink(sinkName: String, sinkConfig: SinkConfiguration) {
        sinks[sinkName] = sinkConfig
    }

    /** DSL: add a sink configuration from dispatcher and renderer. */
    public fun sink(sinkName: String, dispatcher: DispatchString, renderer: RenderString) {
        sinks[sinkName] = SinkConfiguration(dispatcher, renderer)
    }

    /** DSL: add logging configuration specified in [block]. */
    public fun logging(block: LoggingConfig.() -> Unit) {
        val loggingConfig = LoggingConfig()
        loggingConfig.apply(block)
        configs.add(loggingConfig)
    }

    /** Returns the minimum level of all level ranges in all configurations. */
    public fun minimumLevelOf(loggerName: String): Level = configs
        .filter { it.nameMatch.matches(loggerName) }
        .flatMap { it.ranges }
        .minOfOrNull { it.minLevel } ?: Level.NONE

    /** Clear all configurations. */
    internal fun reset() {
        sinks.clear()
        configs.clear()
    }
}

/**
 * DSL: set up configuration.
 *
 * @param append if `true`, append this configuration to any existing one.
 *               Default is `false`, causing this configuration replace any existing one.
 */
public fun loggingConfiguration(append: Boolean = false, block: KloggingConfiguration.() -> Unit) {
    if (!append) KloggingConfiguration.reset()
    KloggingConfiguration.apply(block)
}
