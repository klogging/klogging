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

/** Name of the JSON configuration file on the classpath. */
internal const val JSON_CONFIG_FILENAME: String = "klogging.json"

/** Name of the HOCON configuration file on the classpath. */
internal const val HOCON_CONFIG_FILENAME: String = "klogging.conf"

/**
 * Read the contents of a file at the path, if found.
 *
 * @param filePath path of the file to read
 * @return file contents, if it was found
 */
internal expect fun fileText(filePath: String?): String?

internal data class ConfigFile(
    val path: String,
    val contents: String,
)

/**
 * Find a configuration file and read its contents, if found.
 *
 * @param configPath path of the file to read
 * @return config file contents, if it was found
 */
internal expect fun findConfigFile(configPath: String? = null): ConfigFile?

/**
 * Read configuration from a string read from a configuration file.
 *
 * @param configFile object with file path and contents of a JSON or HOCON file
 * @return a [KloggingConfiguration] object read from the file content
 */
internal expect fun configureFromFile(configFile: ConfigFile?): KloggingConfiguration?

/**
 * Lazily load configuration from a file.
 */
public val configLoadedFromFile: KloggingConfiguration? by lazy {
    configureFromFile(findConfigFile())
}
