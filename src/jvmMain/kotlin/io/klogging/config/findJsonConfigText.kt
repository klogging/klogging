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

import java.io.File
import kotlin.text.Charsets.UTF_8

/**
 * Look for the JSON configuration file in one of two places:
 * 1. specified by the environment variable [ENV_KLOGGING_CONFIG_JSON_PATH]; or
 * 2. called [JSON_CONFIG_FILENAME] in the classpath.
 *
 * If found, return the contents as UTF-8 text; else return `null`.
 */
public actual fun findJsonConfigText(): String? =
    getenv(ENV_KLOGGING_CONFIG_JSON_PATH)
        ?.let { File(it) }
        ?.let { if (it.exists()) it.readText(UTF_8) else null }
        ?: JSON_CONFIG_FILENAME::class.java.getResource(JSON_CONFIG_FILENAME)?.readText(UTF_8)
