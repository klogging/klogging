/*

   Copyright 2021-2022 Michael Strasser.

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
import io.klogging.internal.KloggingEngine
import io.klogging.internal.info
import io.klogging.internal.warn
import io.klogging.rendering.RenderString
import io.klogging.sending.EventSender
import io.klogging.sending.SendString

/**
 * Set the default Klogging log level from the environment using name
 * [ENV_KLOGGING_MIN_LOG_LEVEL] if present, else default to [INFO].
 */
internal val defaultKloggingMinLogLevel: Level = try {
    getenv(ENV_KLOGGING_MIN_LOG_LEVEL)?.let { Level.valueOf(it) } ?: INFO
} catch (_: Throwable) {
    INFO
}

/**
 * Root DSL function for creating a [KloggingConfiguration].
 *
 * @param append if `true`, append this configuration to any existing one.
 *               Default is `false`, causing this configuration replace any existing one.
 *
 * @param block DSL functions with values to apply to this configuration.
 */
@ConfigDsl
public fun loggingConfiguration(append: Boolean = false, block: KloggingConfiguration.() -> Unit) {
    info("Configuration", "Setting configuration using the DSL with append=$append")
    val config = KloggingConfiguration()
    config.apply(block)
    config.validateSinks()
    if (append) KloggingEngine.appendConfig(config)
    else KloggingEngine.setConfig(config)
}

/**
 * Klogging configuration for a runtime.
 */
public class KloggingConfiguration {

    internal val sinks = mutableMapOf<String, SinkConfiguration>()
    internal val configs = mutableListOf<LoggingConfig>()

    internal var kloggingMinLogLevel: Level = defaultKloggingMinLogLevel

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
     * @param sender object that sends an event as string somewhere
     */
    @ConfigDsl
    public fun sink(sinkName: String, renderer: RenderString, sender: SendString) {
        sinks[sinkName] = SinkConfiguration(renderer, sender)
    }

    @ConfigDsl
    public fun sink(sinkName: String, eventSender: EventSender) {
        sinks[sinkName] = SinkConfiguration(eventSender = eventSender)
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
        .filter { it.nameMatcher(loggerName) }
        .flatMap { it.ranges }
        .minOfOrNull { it.minLevel } ?: NONE

    /** Validate that sinks referred to in logging configurations have been defined. */
    internal fun validateSinks() {
        val loggingSinks = configs
            .flatMap { it.ranges }
            .flatMap { it.sinkNames }
            .toSet()
        val extraSinks = loggingSinks - sinks.keys
        extraSinks.forEach {
            warn(
                "Configuration",
                "Sink `$it` was not defined and will be ignored"
            )
        }
    }

    /**
     * Append another configuration to this one.
     *
     * * `sinks` are combined: any with the same name replace those in this config.
     * * `configs` are appended to those in this config.
     */
    internal fun append(other: KloggingConfiguration) {
        sinks.putAll(other.sinks)
        configs.addAll(other.configs)
        if (kloggingMinLogLevel > other.kloggingMinLogLevel)
            kloggingMinLogLevel = other.kloggingMinLogLevel
    }
}
