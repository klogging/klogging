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
import java.io.File
import kotlin.text.Charsets.UTF_8

/**
 * Read the contents of a file on the classpath as UTF-8 text.
 *
 * @param resourcePath path to the resource
 * @return the text in the file, or `null` if the file is not found.
 */
internal fun readResourceText(resourcePath: String): String? =
    // Not 100% sure if using the system classloader will always work.
    ClassLoader.getSystemClassLoader()
        .getResourceAsStream(resourcePath)
        ?.bufferedReader(UTF_8)
        ?.let {
            debug("Configuration", "Reading configuration from $resourcePath on the classpath")
            it.readText()
        }

/**
 * Read the contents of the file specified by absolute [filePath], if found.
 */
internal actual fun fileText(filePath: String?): String? = filePath?.let { path ->
    File(path).let { file ->
        if (file.exists()) {
            debug("Configuration", "Reading configuration from $filePath")
            file.readText(UTF_8)
        } else {
            warn("Configuration", "Specified configuration file $filePath not found")
            null
        }
    }
}

/**
 * Look for the JSON or HOCON configuration file in one of three places:
 * 1. the file specified by [configPath]; or
 * 2. specified by the environment variable [ENV_KLOGGING_CONFIG_JSON_PATH] (deprecated); or
 * 3. specified by the environment variable [ENV_KLOGGING_CONFIG_PATH]; or
 * 4. called [JSON_CONFIG_FILENAME] on the classpath; or
 * 5. called [HOCON_CONFIG_FILENAME] on the classpath.
 *
 * If found, return the contents as UTF-8 text; else return `null`.
 */
internal actual fun findFileConfigText(configPath: String?): String? {
    val filePath = configPath
        ?: getenv(ENV_KLOGGING_CONFIG_JSON_PATH)
        ?: getenv(ENV_KLOGGING_CONFIG_PATH)
    return fileText(filePath)
        ?: readResourceText(JSON_CONFIG_FILENAME)
        ?: readResourceText(HOCON_CONFIG_FILENAME)
}

internal actual fun configureFromFile(fileContents: String): KloggingConfiguration? =
    JsonConfiguration.configure(fileContents) ?: HoconConfiguration.configure(fileContents)
