/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.internal

import io.klogging.AtomicMutableList
import io.klogging.AtomicMutableMap
import io.klogging.Level
import io.klogging.config.KloggingConfiguration
import io.klogging.config.LoggingConfig
import io.klogging.config.SinkConfiguration
import io.klogging.config.configLoadedFromFile
import io.klogging.config.updateFromEnvironment
import io.klogging.context.ContextItemExtractor
import io.klogging.context.ItemExtractor
import kotlin.coroutines.CoroutineContext

/**
 * Object that is the centre of Klogging processing.
 *
 * All static state should be managed here.
 */
internal object KloggingEngine {

    /** Default, empty configuration. */
    private val DEFAULT_CONFIG = KloggingConfiguration()

    /** Key for the current state (and only!) entry in the map. */
    private const val CURRENT_STATE = "CURRENT_STATE"

    /** Map with the current Klogging configuration. */
    private val currentState: MutableMap<String, KloggingConfiguration> =
        AtomicMutableMap(CURRENT_STATE to DEFAULT_CONFIG)

    /** Lazily loaded property that is only set when we have a Klogging state. */
    internal val configuration: KloggingConfiguration by lazy {
        debug("KloggingEngine", "Lazy-loading current configuration")
        configLoadedFromFile?.let { config ->
            setConfig(config)
            info("Configuration", "Configuration read from file")
        }
        currentConfig
    }

    /**
     * Map of functions that extract event items from other coroutine context elements.
     */
    @Suppress("TYPE_ALIAS")
    internal val otherContextExtractors: AtomicMutableMap<CoroutineContext.Key<*>, ContextItemExtractor> =
        AtomicMutableMap()

    internal val otherItemExtractors: AtomicMutableList<ItemExtractor> = AtomicMutableList()

    /**
     * Map of context items to include in all log events.
     */
    internal val baseContextItems: MutableMap<String, Any?> = mutableMapOf()

    /**
     * Set a new configuration, replacing the existing one.
     * @param config new configuration
     */
    internal fun setConfig(config: KloggingConfiguration) {
        val updatedConfig = config.updateFromEnvironment()
        currentState[CURRENT_STATE] = updatedConfig
        setSinks(updatedConfig.sinks)
    }

    /**
     * Append a new configuration to the existing one.
     * @param config configuration to append
     */
    internal fun appendConfig(config: KloggingConfiguration) {
        currentConfig.append(config.updateFromEnvironment())
        setSinks(currentConfig.sinks)
    }

    /**
     * Return the current configuration, ensuring it is never null.
     */
    @Suppress("CUSTOM_GETTERS_SETTERS")
    private val currentConfig: KloggingConfiguration
        get() = currentState[CURRENT_STATE] ?: DEFAULT_CONFIG

    /** Map of the current [Sink]s used for sending log events. */
    private val currentSinks: MutableMap<String, Sink> = AtomicMutableMap()

    /** Set new sinks, from configurations. */
    private fun setSinks(sinkConfigs: Map<String, SinkConfiguration>) {
        currentSinks.clear()
        currentSinks.putAll(
            sinkConfigs.map { (name, config) ->
                name to Sink(name, config.eventSender)
            },
        )
        // Also clear the dispatcher’s cache of sinks.
        Dispatcher.clearCache()
    }

    /**
     * Return the minimum [Level] of a logger from the current configuration.
     * @param loggerName name of the logger
     * @return minimum level
     */
    internal fun minimumLevelOf(loggerName: String): Level = currentConfig.minimumLevelOf(loggerName)

    /**
     * Return the current [Sink]s.
     * @return map of sinks keyed by name
     */
    internal fun sinks(): Map<String, Sink> = currentSinks

    /**
     * Return the current [SinkConfiguration]s.
     * @return map of configurations key by name
     */
    internal fun sinkConfigs(): Map<String, SinkConfiguration> = currentConfig.sinks

    /**
     * Return the current [LoggingConfig]s.
     * @return list configurations
     */
    internal fun configs(): List<LoggingConfig> = currentConfig.configs

    /**
     * Return the current minimum Klogging internal log [Level].
     * @return minimum level
     */
    internal fun kloggingMinLogLevel(): Level = currentConfig.kloggingMinLogLevel

    /**
     * Return the current minimum direct logging [Level].
     * @return minimum direct logging level
     */
    internal fun minDirectLogLevel(): Level = currentConfig.minDirectLogLevel
}
