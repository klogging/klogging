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

import io.klogging.internal.info
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
            info("Configuration", "Reading JSON configuration from $resourcePath on the classpath")
            it.readText()
        }

/**
 * Look for the JSON configuration file in one of two places:
 * 1. specified by the environment variable [ENV_KLOGGING_CONFIG_JSON_PATH]; or
 * 2. called [JSON_CONFIG_FILENAME] in the classpath.
 *
 * If found, return the contents as UTF-8 text; else return `null`.
 */
public actual fun findJsonConfigText(): String? {
    val filePath = getenv(ENV_KLOGGING_CONFIG_JSON_PATH)
    return filePath
        ?.let { File(it) }
        ?.let {
            if (it.exists()) {
                info("Configuration", "Reading JSON configuration from $filePath")
                it.readText(UTF_8)
            } else {
                warn("Configuration", "Specified JSON configuration file $filePath not found")
                null
            }
        }
        ?: readResourceText(JSON_CONFIG_FILENAME)
}
