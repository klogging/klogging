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

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.klogging.context.Context
import io.klogging.internal.debug
import io.klogging.internal.warn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon

@OptIn(ExperimentalSerializationApi::class)
public object HoconConfiguration {
    private val hocon = Hocon {}

    /**
     * Read configuration from HOCON into a [HoconConfiguration] object.
     *
     * @param configHocon HOCON containing Klogging configuration.
     *
     * @return an object used to configure Klogging.
     */
    internal fun readConfig(configHocon: String): FileConfiguration? =
        try {
            val config = ConfigFactory.parseString(configHocon).resolve()
            hocon.decodeFromConfig(FileConfiguration.serializer(), config)
        } catch (ex: ConfigException) {
            warn("HoconConfiguration", "Exception parsing HOCON", ex)
            null
        }

    /** Load [KloggingConfiguration] from HOCON configuration string. */
    public fun configure(configHocon: String): KloggingConfiguration? =
        try {
            readConfig(configHocon)?.let { (configName, minLogLevel, minDirectLogLevel, sinks, logging, baseContext) ->
                if (baseContext.isNotEmpty()) {
                    val contextItems = baseContext.entries.map { it.key to evalEnv(it.value) }.toTypedArray()
                    Context.addBaseContext(*contextItems)
                }
                val config = KloggingConfiguration()
                if (configName != null) {
                    builtInConfigurations[configName]?.let { config.apply(it) }
                } else {
                    config.kloggingMinLogLevel = minLogLevel
                    config.minDirectLogLevel = minDirectLogLevel

                    sinks.forEach { (key, value) ->
                        value.toSinkConfiguration()?.let {
                            debug("HOCON Configuration", "Setting sink `$key` with $value")
                            config.sinks[key] = it
                        }
                    }

                    logging.forEach {
                        debug("HOCON Configuration", "Adding logging config $it")
                        config.configs.add(it.toLoggingConfig())
                    }
                }
                config
            }
        } catch (e: Exception) {
            warn("HoconConfiguration", "Exception parsing HOCON", e)
            null
        }
}
