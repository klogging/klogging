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

package io.klogging.config

import io.klogging.Level
import io.klogging.Level.FATAL
import io.klogging.Level.TRACE
import io.klogging.internal.debug
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RenderPattern
import io.klogging.rendering.RenderString
import io.klogging.rendering.renderHec
import io.klogging.sending.EventSender
import io.klogging.sending.SplunkEndpoint
import io.klogging.sending.SplunkHec
import kotlinx.serialization.Serializable

/**
 * Data class for file representation of [KloggingConfiguration], either in JSON or HOCON
 * format.
 *
 * Fields are optional so that when `append` is specified, only new configuration
 * needs to be included in the file.
 *
 * @property configName optional configuration name
 * @property kloggingMinLogLevel minimum level for Klogging internal logger
 * @property minDirectLogLevel minimum level for Klogging to send log events directly (i.e. not via coroutine channels)
 * @property sinks map of named [FileSinkConfiguration] objects
 * @property logging list of [FileLoggingConfig] objects
 */
@Serializable
public data class FileConfiguration(
    val configName: String? = null,
    val kloggingMinLogLevel: Level = defaultKloggingMinLogLevel,
    val minDirectLogLevel: Level = defaultMinDirectLogLevel,
    val sinks: Map<String, FileSinkConfiguration> = mapOf(),
    val logging: List<FileLoggingConfig> = listOf(),
    val baseContext: Map<String, String> = mapOf()
)

/**
 * Data class for a file representation of a [SinkConfiguration].
 *
 * @property renderWith name of a built-in renderer
 * @property sendTo name of a built-in sender
 * @property seqServer URL of a [Seq](https://datalust.co/seq) log aggregation server
 * @property splunkServer Splunk [HEC](https://docs.splunk.com/Documentation/Splunk/8.2.2/Data/HECExamples)
 *                        endpoint configuration
 */
@Serializable
public data class FileSinkConfiguration(
    val renderWith: String? = null,
    val renderPattern: String? = null,
    val sendTo: String? = null,
    val seqServer: String? = null,
    val apiKey: String? = null,
    val checkCertificate: Boolean? = null,
    val splunkServer: SplunkEndpoint? = null,
    val renderHec: RenderHec? = null,
    val eventSender: String? = null,
) {
    /**
     * Extract sink configuration from a file into a [SinkConfiguration] object.
     * @return a [SinkConfiguration] object as specified in the file
     */
    internal fun toSinkConfiguration(): SinkConfiguration? {
        if (eventSender != null) {
            val sender = loadByClassName<EventSender>(eventSender)
            if (sender != null) {
                return SinkConfiguration(eventSender = sender)
            }
        }
        val renderer = renderPattern?.let { RenderPattern(it) }
            ?: builtInRenderers[renderWith]
            ?: loadByClassName(renderWith)
            ?: renderHec?.renderer
        if (splunkServer != null) {
            return SinkConfiguration(eventSender = SplunkHec(splunkServer.evalEnv(), renderer ?: renderHec()))
        }
        if (seqServer != null) {
            return seq(
                evalEnv(seqServer),
                apiKey?.let { evalEnv(it) },
                checkCertificate ?: true,
                renderer ?: RENDER_CLEF,
            )
        }
        val sender = builtInSenders[sendTo]
            ?: loadByClassName(sendTo)
        return if (renderer != null && sender != null) {
            SinkConfiguration(renderer, sender)
        } else {
            null
        }
    }

    /**
     * Compact rendering to string, masking any API key.
     * @return string rendering
     */
    public override fun toString(): String {
        val props = buildList {
            if (renderWith != null) {
                add("renderWith=$renderWith")
            }
            if (renderPattern != null) {
                add("renderPattern=\"$renderPattern\"")
            }
            if (sendTo != null) {
                add("sendTo=$sendTo")
            }
            if (seqServer != null) {
                add("seqServer=$seqServer")
            }
            if (apiKey != null) {
                add("apiKey=********")
            }
            if (checkCertificate != null) {
                add("checkCertificate=$checkCertificate")
            }
            if (splunkServer != null) {
                add("splunkServer=$splunkServer")
            }
            if (renderHec != null) {
                add("renderHec=$renderHec")
            }
        }
        return "FileSinkConfiguration(${props.joinToString(", ")})"
    }
}

/**
 * Data class for file representation of a [RenderHec].
 *
 * @property index name of the Splunk `index` to send to
 * @property sourceType name of the Splunk `sourceType`
 * @property source name of the Splunk `source`
 */
@Serializable
@Suppress("CUSTOM_GETTERS_SETTERS")
public data class RenderHec(
    val index: String? = null,
    val sourceType: String? = null,
    val source: String? = null,
) {
    /**
     * Extract a [RenderString] instance from the file configuration.
     */
    public val renderer: RenderString
        get() = renderHec(index, sourceType, source)
}

/**
 * Data class for a file representation of a [LoggingConfig].
 *
 * @property fromLoggerBase logger name to use as a base for matching
 * @property stopOnMatch stop searching for loggers after the first match
 * @property exactLogger logger name to match exactly
 * @property matchLogger regular expression to match against logger name
 * @property levelRanges level range specifications
 */
@Serializable
public data class FileLoggingConfig(
    val fromLoggerBase: String? = null,
    val stopOnMatch: Boolean? = false,
    val exactLogger: String? = null,
    val matchLogger: String? = null,
    val levelRanges: List<FileLevelRange>,
) {
    /**
     * Extract logging configuration from a file into a [LoggingConfig] object.
     * @return a [LoggingConfig] object as specified in the file
     */
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
        config.ranges.addAll(levelRanges.map { range -> range.toLevelRange() })
        return config
    }
}

/**
 * Data class for a file representation of a [LevelRange].
 *
 * @property fromMinLevel inclusive minimum level of log events to send
 * @property toMaxLevel inclusive maximum level of log events to send
 * @property atLevel exact level of log events to send
 * @property toSinks list of sink names where log events are sent
 */
@Serializable
public data class FileLevelRange(
    val fromMinLevel: Level? = null,
    val toMaxLevel: Level? = null,
    val atLevel: Level? = null,
    val toSinks: List<String>? = null,
) {
    /**
     * Extract level range configuration from a file into a [LevelRange] object.
     * @return a [LevelRange] object as specifed in the file
     */
    internal fun toLevelRange(): LevelRange {
        val range = when {
            // `atLevel` has priority over everything else
            atLevel != null -> LevelRange(atLevel, atLevel)
            // min and max levels
            else -> LevelRange(fromMinLevel ?: TRACE, toMaxLevel ?: FATAL)
        }
        toSinks?.let { range.sinkNames.addAll(it) }
        debug("File Configuration", "Setting log levels range $range for sinks $toSinks")
        return range
    }
}

/**
 * Load a class from the classpath by name.
 *
 * @param className fully-qualified name of the class, which might be null
 * @return an instance of that class if found, or null
 */
internal expect fun <T : Any> loadByClassName(className: String?): T?
