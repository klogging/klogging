/*

   Copyright 2022 Michael Strasser.

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
import io.klogging.Level.FATAL
import io.klogging.Level.TRACE
import io.klogging.internal.debug
import io.klogging.internal.warn
import io.klogging.rendering.RENDER_CLEF
import io.klogging.sending.SplunkEndpoint
import io.klogging.sending.splunkHec
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Data class for JSON representation of [KloggingConfiguration].
 *
 * Fields are optional so that when `append` is specified, only new configuration
 * needs to be included in the JSON.
 */
@Serializable
public data class JsonConfiguration(
    val configName: String? = null,
    val kloggingMinLogLevel: Level = defaultKloggingMinLogLevel,
    val sinks: Map<String, JsonSinkConfiguration> = mapOf(),
    val logging: List<JsonLoggingConfig> = listOf(),
)

/**
 * Data class for JSON representation of a [SinkConfiguration].
 *
 * @property renderWith name of a built-in renderer
 * @property sendTo name of a built-in sender
 * @property seqServer URL of a [Seq](https://datalust.co/seq) log aggregation server
 * @property splunkServer Splunk [HEC](https://docs.splunk.com/Documentation/Splunk/8.2.2/Data/HECExamples)
 *                        endpoint configuration
 */
@Serializable
public data class JsonSinkConfiguration(
    val renderWith: String? = null,
    val sendTo: String? = null,
    @Deprecated("Use `sendTo` instead")
    val dispatchTo: String? = null,
    val seqServer: String? = null,
    val splunkServer: SplunkEndpoint? = null,
) {
    internal fun toSinkConfiguration(): SinkConfiguration? {
        if (splunkServer != null)
            return SinkConfiguration(eventSender = splunkHec(splunkServer.evalEnv()))
        val renderer = BUILT_IN_RENDERERS[renderWith]
        if (seqServer != null)
            return seq(seqServer, renderer ?: RENDER_CLEF)
        if (dispatchTo != null)
            warn("Configuration", "Please use `sendTo` in JSON config instead of `dispatchTo`, which has been deprecated")
        val sender = BUILT_IN_SENDERS[sendTo ?: dispatchTo]
        return if (renderer != null && sender != null) SinkConfiguration(renderer, sender)
        else null
    }
}

/** Data class for JSON representation of a [LoggingConfig]. */
@Serializable
public data class JsonLoggingConfig(
    val fromLoggerBase: String? = null,
    val stopOnMatch: Boolean? = false,
    val exactLogger: String? = null,
    val matchLogger: String? = null,
    val levelRanges: List<JsonLevelRange>,
) {
    internal fun toLoggingConfig(): LoggingConfig {
        val config = LoggingConfig()
        when {
            // `matchLogger` has priority over `exactLogger`, which
            // has priority over `fromLoggerBase`,
            // which has priority over `matchLogger`
            exactLogger != null -> config.exactLogger(exactLogger, stopOnMatch ?: false)
            fromLoggerBase != null -> config.fromLoggerBase(fromLoggerBase, stopOnMatch ?: false)
            matchLogger != null -> config.matchLogger(matchLogger, stopOnMatch ?: false)
            // else match all loggers by default
        }
        config.ranges.addAll(levelRanges.map { it.toLevelRange() })
        return config
    }
}

/** Data class for JSON representation of a [LevelRange]. */
@Serializable
public data class JsonLevelRange(
    val fromMinLevel: Level? = null,
    val atLevel: Level? = null,
    val toSinks: List<String>,
) {
    internal fun toLevelRange(): LevelRange {
        val range = when {
            // `atLevel` has priority over `fromMinLevel`
            atLevel != null -> LevelRange(atLevel, atLevel)
            fromMinLevel != null -> LevelRange(fromMinLevel, FATAL)
            // All levels
            else -> LevelRange(TRACE, FATAL)
        }
        range.sinkNames.addAll(toSinks)
        return range
    }
}

/** Set up the JSON deserialiser to be accepting of unknown and malformed values. */
private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

/**
 * Read configuration from JSON into a [JsonConfiguration] object.
 *
 * @param configJson JSON containing Klogging configuration.
 *
 * @return an object used to configure Klogging.
 */
@OptIn(ExperimentalSerializationApi::class)
internal fun readConfig(configJson: String): JsonConfiguration? =
    try {
        json.decodeFromString(configJson)
    } catch (ex: SerializationException) {
        warn("JsonConfiguration", "Exception parsing JSON", ex)
        null
    }

/** Load [KloggingConfiguration] from JSON configuration string. */
public fun configureFromJson(configJson: String): KloggingConfiguration? =
    readConfig(configJson)?.let { (configName, minLogLevel, sinks, logging) ->
        val config = KloggingConfiguration()
        if (configName != null)
            BUILT_IN_CONFIGURATIONS[configName]?.let { config.apply(it) }
        else {
            config.kloggingMinLogLevel = minLogLevel

            sinks.forEach { (key, value) ->
                value.toSinkConfiguration()?.let {
                    debug("JsonConfiguration", "Setting sink `$key` with $value")
                    config.sinks[key] = it
                }
            }

            logging.forEach {
                debug("JsonConfiguration", "Adding logging config $it")
                config.configs.add(it.toLoggingConfig())
            }
        }
        config
    }
