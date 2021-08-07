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
import io.klogging.internal.debug
import io.klogging.internal.warn
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Data class for JSON representation of [KloggingConfiguration].
 *
 * Fields are optional so that when `append` is specified, only new configuration
 * needs to be added.
 */
@Serializable
public data class JsonConfiguration(
    val append: Boolean = false,
    val kloggingLevel: Level? = null,
    val sinks: Map<String, JsonSinkConfiguration> = mapOf(),
    val logging: List<JsonLoggingConfig> = listOf(),
)

/**
 * Data class for JSON representation of a [SinkConfiguration].
 */
@Serializable
public data class JsonSinkConfiguration(
    val renderWith: String?,
    val dispatchTo: String?,
) {
    internal fun toSinkConfiguration(): SinkConfiguration? {
        val renderer = BUILT_IN_RENDERERS[renderWith]
        val dispatcher = BUILT_IN_DISPATCHERS[dispatchTo]
        return if (renderer != null && dispatcher != null) SinkConfiguration(renderer, dispatcher)
        else null
    }
}

/**
 * Data class for JSON representation of a [LoggingConfig].
 */
@Serializable
public data class JsonLoggingConfig(
    val fromLoggerBase: String? = null,
    val exactLogger: String? = null,
    val levelRanges: List<JsonLevelRange>,
) {
    internal fun toLoggingConfig(): LoggingConfig {
        val config = LoggingConfig()
        when {
            exactLogger != null -> config.exactLogger(exactLogger)
            fromLoggerBase != null -> config.fromLoggerBase(fromLoggerBase)
            // else will match all loggers by default
        }
        config.ranges.addAll(levelRanges.map { it.toLevelRange() })
        return config
    }
}

/**
 * Data class for JSON representation of a [LevelRange].
 */
@Serializable
public data class JsonLevelRange(
    val fromMinLevel: Level? = null,
    val atLevel: Level? = null,
    val toSinks: List<String>,
) {
    internal fun toLevelRange(): LevelRange {
        val range = when {
            atLevel != null -> LevelRange(atLevel, atLevel)
            fromMinLevel != null -> LevelRange(fromMinLevel, Level.FATAL)
            else -> LevelRange(Level.TRACE, Level.FATAL)
        }
        range.sinkNames.addAll(toSinks)
        return range
    }
}

/**
 * Read configuration from JSON into a [JsonConfiguration] object.
 *
 * @param configJson JSON containing Klogging configuration.
 *
 * @return an object used to configure Klogging.
 */
internal fun readConfig(configJson: String): JsonConfiguration? =
    try {
        Json { ignoreUnknownKeys = true }.decodeFromString(configJson)
    } catch (ex: SerializationException) {
        warn(CONFIG_LOGGER, "Exception parsing JSON configuration", ex)
        null
    }

/**
 * Load [KloggingConfiguration] from JSON configuration string.
 */
public fun configureFromJson(configJson: String) {
    readConfig(configJson)?.let { config ->
        if (!config.append) KloggingConfiguration.reset()
        if (config.kloggingLevel != null) kloggingLogLevel = config.kloggingLevel
        config.sinks.forEach { entry ->
            entry.value.toSinkConfiguration()?.let {
                debug(CONFIG_LOGGER, "Setting sink `${entry.key}` with ${entry.value}")
                KloggingConfiguration.sinks[entry.key] = it
            }
        }
        config.logging.forEach { logging ->
            debug(CONFIG_LOGGER, "Adding logging config $logging")
            KloggingConfiguration.configs.add(logging.toLoggingConfig())
        }
    }
}
