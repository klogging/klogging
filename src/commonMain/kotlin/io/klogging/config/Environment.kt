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

internal const val ENV_KLOGGING_MIN_LOG_LEVEL = "KLOGGING_MIN_LOG_LEVEL"
internal const val ENV_KLOGGING_CONFIG_JSON_PATH = "KLOGGING_CONFIG_JSON_PATH"
internal const val ENV_KLOGGING_COROUTINE_THREADS = "KLOGGING_COROUTINE_THREADS"

internal const val ENV_FLAG_JVM_USE_EXECUTOR_THREAD_POOL = "FLAG_JVM_USE_EXECUTOR_THREAD_POOL"

/**
 * ☠️ This function should be `private`, but as it is implemented in multiplatform, must be
 * `internal`. Do not call this function directly outside this file.
 *
 * @see [ENV]
 */
internal expect fun getenv(): Map<String, String>

/** A map of operating system environment variables and values for the current process. */
internal val ENV: Map<String, String> by lazy { getenv() }

/** Return the value of an item in the running environment, or `null` if the name is not found. */
public fun getenv(name: String): String? = ENV[name]

/** Return the value of an item in the running environment, or a default value if not found. */
public fun getenv(name: String, default: String): String = ENV[name] ?: default

/**
 * Return the value of an item in the running environment as an [Int], or a default value
 * if not found or if the value cannot be parsed as an [Int].
 */
public fun getenvInt(name: String, default: Int): Int = ENV[name]?.toIntOrNull() ?: default

/**
 * Return the value of an item in the running environment as a [Boolean], or a default value
 * if not found.
 */
public fun getenvBoolean(name: String, default: Boolean): Boolean =
    ENV[name]?.toBoolean() ?: default
