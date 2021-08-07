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

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Data class for JSON representation of [KloggingConfiguration].
 */
@Serializable
public data class JsonConfiguration(
    val append: Boolean = false,
    val sinks: Map<String, JsonSinkConfiguration>,
    val logging: List<JsonLoggingConfig>,
)

/**
 * Data class for JSON representation of a [SinkConfiguration].
 */
@Serializable
public data class JsonSinkConfiguration(
    val renderWith: String?,
    val dispatchTo: String?,
)

/**
 * Data class for JSON representation of a [LoggingConfig].
 */
@Serializable
public data class JsonLoggingConfig(
    val fromLoggerBase: String? = null,
    val exactLogger: String? = null,
    val levelRanges: List<JsonLevelRange>,
)

/**
 * Data class for JSON representation of a [LevelRange].
 */
@Serializable
public data class JsonLevelRange(
    val fromMinLevel: String? = null,
    val atLevel: String? = null,
    val toSinks: List<String>,
)

/**
 * Read configuration from JSON into a [JsonConfiguration] object.
 *
 * @param configJson JSON containing Klogging configuration.
 *
 * @return an object used to configure Klogging.
 */
public fun readConfig(configJson: String): JsonConfiguration = Json.decodeFromString(configJson)
