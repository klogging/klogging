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

/** Name of the JSON configuration file on the classpath. */
internal const val JSON_CONFIG_FILENAME: String = "klogging.json"

/** Name of the HOCON configuration file on the classpath. */
internal const val HOCON_CONFIG_FILENAME: String = "klogging.conf"

/** Find a configuration file and read its contents, if found. */
internal expect fun findFileConfigText(): String?

internal expect fun configureFromFile(fileContents: String): KloggingConfiguration?

/** Attempt to load configuration from a file. */
public val configLoadedFromFile: KloggingConfiguration? by lazy {
    findFileConfigText()?.let { configureFromFile(it) }
}
