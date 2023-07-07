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

import io.klogging.internal.debug
import io.klogging.internal.warn
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

public object JsonConfiguration {
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
    internal fun readConfig(configJson: String): FileConfiguration? =
        try {
            json.decodeFromString(configJson)
        } catch (ex: SerializationException) {
            warn("JsonConfiguration", "Exception parsing JSON", ex)
            null
        }

    /** Load [KloggingConfiguration] from JSON configuration string. */
    public fun configure(configJson: String): KloggingConfiguration? =
        readConfig(configJson)?.let { (configName, minLogLevel, minDirectLogLevel, sinks, logging) ->
            val config = KloggingConfiguration()
            if (configName != null) {
                BUILT_IN_CONFIGURATIONS[configName]?.let { config.apply(it) }
            } else {
                config.kloggingMinLogLevel = minLogLevel
                config.minDirectLogLevel = minDirectLogLevel

                sinks.forEach { (key, value) ->
                    value.toSinkConfiguration()?.let {
                        debug("JSON Configuration", "Setting sink `$key` with $value")
                        config.sinks[key] = it
                    }
                }

                logging.forEach {
                    debug("JSON Configuration", "Adding logging config $it")
                    config.configs.add(it.toLoggingConfig())
                }
            }
            config
        }
}
