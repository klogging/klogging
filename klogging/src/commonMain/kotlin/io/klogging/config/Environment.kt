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

import kotlin.math.max

internal const val ENV_KLOGGING_MIN_LOG_LEVEL = "KLOGGING_MIN_LOG_LEVEL"
internal const val ENV_KLOGGING_MIN_DIRECT_LOG_LEVEL = "KLOGGING_MIN_DIRECT_LOG_LEVEL"
internal const val ENV_KLOGGING_CONFIG_JSON_PATH = "KLOGGING_CONFIG_JSON_PATH"
internal const val ENV_KLOGGING_CONFIG_PATH = "KLOGGING_CONFIG_PATH"
internal const val ENV_KLOGGING_COROUTINE_THREADS = "KLOGGING_COROUTINE_THREADS"
internal const val ENV_KLOGGING_EVENT_CHANNEL_CAPACITY = "KLOGGING_EVENT_CHANNEL_CAPACITY"
internal const val ENV_KLOGGING_SINK_CHANNEL_CAPACITY = "KLOGGING_SINK_CHANNEL_CAPACITY"
internal const val ENV_KLOGGING_BATCH_MAX_TIME_MS = "KLOGGING_BATCH_MAX_TIME_MS"
internal const val ENV_KLOGGING_BATCH_MAX_SIZE = "KLOGGING_BATCH_MAX_SIZE"
internal const val ENV_KLOGGING_OUTPUT_FORMAT_PREFIX = "KLOGGING_OUTPUT_FORMAT_"

internal const val ENV_KLOGGING_FF_EXECUTOR_THREAD_POOL = "KLOGGING_FF_EXECUTOR_THREAD_POOL"
internal const val ENV_KLOGGING_FF_GLOBAL_SCOPE = "KLOGGING_FF_GLOBAL_SCOPE"

internal const val ENV_KLOGGING_DISPATCHERS_IO = "KLOGGING_DISPATCHERS_IO"

/**
 * ☠️ This function should be `private`, but as it is implemented in multiplatform, must be
 * `internal`. Do not call this function directly outside this file.
 *
 * @see [ENV]
 */
internal expect fun getenv(): Map<String, String>

/** A map of operating system environment variables and values for the current process. */
@Suppress("VARIABLE_NAME_INCORRECT_FORMAT")
internal val ENV: Map<String, String> by lazy { getenv() }

/**
 * Return the value of an item in the running environment, or `null` if the name is not found.
 *
 * @param name name of the environment variable
 * @return value of that variable, if any
 */
public fun getenv(name: String): String? = ENV[name]

/** Return the value of an item in the running environment, or a default value if not found.
 *
 * @param name name of the environment variable
 * @param default default value to return if no environment variable is found
 * @return value of that variable, if any
 */
public fun getenv(name: String, default: String): String = ENV[name] ?: default

/**
 * Return the value of an item in the running environment as an [Int], or a default value
 * if not found or if the value cannot be parsed as an [Int].
 *
 * @param name name of the environment variable
 * @param default default value
 * @param minValue minimum value
 * @return [Int] value of the environment variable or default
 */
public fun getenvInt(name: String, default: Int, minValue: Int = 1): Int =
    max(ENV[name]?.toIntOrNull() ?: default, minValue)

/**
 * Return the value of an item in the running environment as a [Long], or a default value
 * if not found or if the value cannot be parsed as a [Long].
 *
 * @param name name of the environment variable
 * @param default default value
 * @param minValue minimum value
 * @return [Long] value of the environment variable or default
 */
public fun getenvLong(name: String, default: Long, minValue: Long = 1L): Long =
    max(ENV[name]?.toLongOrNull() ?: default, minValue)

/**
 * Return the value of an item in the running environment as a [Boolean], or a default value
 * if not found.
 *
 * @param name name of the environment variable
 * @param default default value to return if no environment variable is found
 * @return [Boolean] value of that variable, if any
 */
@Suppress("FUNCTION_BOOLEAN_PREFIX")
public fun getenvBoolean(name: String, default: Boolean): Boolean =
    ENV[name]?.toBoolean() ?: default

/**
 * Evaluate env vars in a string. Env vars must be surrounded by braces and preceded
 * with ‘$’, e.g. `${KLOGGING_BLAH}`.
 *
 * @param string string that may contain env variables
 * @param env map of environment variables and values, default [ENV]
 * @return string with environment variables replaced with their values
 */
public fun evalEnv(string: String, env: Map<String, String> = ENV): String = env.entries
    .fold(string) { str, (key, value) -> str.replace("\${$key}", value) }
