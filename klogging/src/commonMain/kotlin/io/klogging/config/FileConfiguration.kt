/*

   Copyright 2021-2023 Michael Strasser.

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
import io.klogging.rendering.RENDER_CLEF
import io.klogging.sending.SplunkEndpoint
import io.klogging.sending.splunkHec
import kotlinx.serialization.Serializable

/**
 * Data class for file representation of [KloggingConfiguration], either in JSON or HOCON
 * format.
 *
 * Fields are optional so that when `append` is specified, only new configuration
 * needs to be included in the file.
 */
@Serializable
public data class FileConfiguration(
    val configName: String? = null,
    val kloggingMinLogLevel: Level = defaultKloggingMinLogLevel,
    val minDirectLogLevel: Level = defaultMinDirectLogLevel,
    val sinks: Map<String, FileSinkConfiguration> = mapOf(),
    val logging: List<FileLoggingConfig> = listOf(),
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
    val sendTo: String? = null,
    val seqServer: String? = null,
    val splunkServer: SplunkEndpoint? = null,
) {
    internal fun toSinkConfiguration(): SinkConfiguration? {
        if (splunkServer != null) {
            return SinkConfiguration(eventSender = splunkHec(splunkServer.evalEnv()))
        }
        val renderer = BUILT_IN_RENDERERS[renderWith]
        if (seqServer != null) {
            return seq(seqServer, renderer ?: RENDER_CLEF)
        }
        val sender = BUILT_IN_SENDERS[sendTo]
        return if (renderer != null && sender != null) {
            SinkConfiguration(renderer, sender)
        } else {
            null
        }
    }
}

/** Data class for a file representation of a [LoggingConfig]. */
@Serializable
public data class FileLoggingConfig(
    val fromLoggerBase: String? = null,
    val stopOnMatch: Boolean? = false,
    val exactLogger: String? = null,
    val matchLogger: String? = null,
    val levelRanges: List<FileLevelRange>,
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

/** Data class for a file representation of a [LevelRange]. */
@Serializable
public data class FileLevelRange(
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
