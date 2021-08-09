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

import io.klogging.Level
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.dispatching.DispatchString
import io.klogging.internal.info
import io.klogging.rendering.RenderString
import kotlin.native.concurrent.ThreadLocal

/**
 * Set the default Klogging log level from the environment using name
 * [ENV_KLOGGING_MIN_LOG_LEVEL] if present, else default to [INFO].
 */
internal val defaultKloggingMinLogLevel: Level = try {
    getenv(ENV_KLOGGING_MIN_LOG_LEVEL)?.let { Level.valueOf(it) } ?: INFO
} catch (ex: Exception) { INFO }

@ThreadLocal
internal var kloggingMinLogLevel: Level = defaultKloggingMinLogLevel

/**
 * Root DSL function for creating a [KloggingConfiguration].
 *
 * @param append if `true`, append this configuration to any existing one.
 *               Default is `false`, causing this configuration replace any existing one.
 */
@ConfigDsl
public fun loggingConfiguration(append: Boolean = false, block: KloggingConfiguration.() -> Unit) {
    info("Setting configuration using the DSL with append=$append")
    if (!append) KloggingConfiguration.reset()
    KloggingConfiguration.apply(block)
}

/**
 * Klogging configuration for a runtime.
 */
public object KloggingConfiguration {

    internal val sinks = mutableMapOf<String, SinkConfiguration>()
    internal val configs = mutableListOf<LoggingConfig>()

    /**
     * DSL function to set minimum logging level for Klogging itself.
     */
    @ConfigDsl
    public fun kloggingMinLevel(minLevel: Level) {
        kloggingMinLogLevel = minLevel
    }

    /**
     * DSL function to specify a sink where log events can be dispatched.
     *
     * @param sinkName name used to refer to the sink
     * @param sinkConfig configuration to use
     */
    @ConfigDsl
    public fun sink(sinkName: String, sinkConfig: SinkConfiguration) {
        sinks[sinkName] = sinkConfig
    }

    /**
     * DSL function to specify a sink where log events can be dispatched.
     *
     * @param sinkName name used to refer to the sink
     * @param renderer object that renders an event into a string
     * @param dispatcher object that sends an event as string somewhere
     */
    @ConfigDsl
    public fun sink(sinkName: String, renderer: RenderString, dispatcher: DispatchString) {
        sinks[sinkName] = SinkConfiguration(renderer, dispatcher)
    }

    /**
     * DSL function to add a logging configuration specified in [configBlock].
     */
    @ConfigDsl
    public fun logging(configBlock: LoggingConfig.() -> Unit) {
        val loggingConfig = LoggingConfig()
        loggingConfig.apply(configBlock)
        configs.add(loggingConfig)
    }

    /** Calculate the minimum level of all level ranges in all configurations. */
    public fun minimumLevelOf(loggerName: String): Level = configs
        .filter { it.nameMatch.matches(loggerName) }
        .flatMap { it.ranges }
        .minOfOrNull { it.minLevel } ?: NONE

    /** Clear all configurations and reset default Klogging log level. */
    internal fun reset() {
        kloggingMinLogLevel = defaultKloggingMinLogLevel
        sinks.clear()
        configs.clear()
    }
}
