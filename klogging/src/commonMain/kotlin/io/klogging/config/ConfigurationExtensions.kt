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

import io.klogging.internal.info
import io.klogging.sending.senderFrom

/**
 * Updates the Klogging configuration based on environment variables.
 *
 * This method checks for specific environment variables starting with the prefix ENV_KLOGGING_OUTPUT_FORMAT_PREFIX and
 * uses their values to update the output format of the corresponding sinks in the configuration.
 *
 * @return the updated KloggingConfiguration object.
 */
internal fun KloggingConfiguration.updateFromEnvironment(): KloggingConfiguration =
    this.sinks.keys.fold(this) { config, sinkName ->
        val envName = ENV_KLOGGING_OUTPUT_FORMAT_PREFIX + sinkName.uppercase()
        getenv(envName)?.let { outputFormat ->
            config.sinks[sinkName]?.let { sinkConfig ->
                config.sinks[sinkName] = sinkConfig.updateRenderer(outputFormat)
                info("Configuration", "Updating sink $sinkName format to $outputFormat")
                config
            }
        } ?: config
    }

/**
 * Updates the renderer of the SinkConfiguration with the specified output format.
 *
 * @param outputFormat The output format of the renderer.
 * @return The updated SinkConfiguration with the new renderer.
 */
internal fun SinkConfiguration.updateRenderer(outputFormat: String): SinkConfiguration =
    builtInRenderers["RENDER_$outputFormat"]?.let { newRenderer ->
        copy(
            renderer = newRenderer,
            stringSender = stringSender,
            eventSender = senderFrom(newRenderer, stringSender),
        )
    } ?: this
