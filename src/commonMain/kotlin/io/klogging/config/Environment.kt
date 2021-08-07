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

internal const val ENV_KLOGGING_LOG_LEVEL = "KLOGGING_LOG_LEVEL"

internal expect fun getenv(): Map<String, String>

internal val ENV: Map<String, String> by lazy { getenv() }

/**
 * Return the value of an item in the running environment, or
 * `null` if the name is not found.
 */
public fun getenv(name: String): String? = ENV[name]

/**
 * Return the value of an item in the running environment, or
 * a default value if not found.
 */
public fun getenv(name: String, default: String): String = ENV[name] ?: default
