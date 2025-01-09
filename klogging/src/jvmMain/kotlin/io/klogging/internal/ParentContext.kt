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

package io.klogging.internal

import io.klogging.config.ENV_KLOGGING_COROUTINE_THREADS
import io.klogging.config.ENV_KLOGGING_FF_EXECUTOR_THREAD_POOL
import io.klogging.config.ENV_KLOGGING_FF_GLOBAL_SCOPE
import io.klogging.config.ENV_KLOGGING_DISPATCHERS_IO
import io.klogging.config.getenvBoolean
import io.klogging.config.getenvInt
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

/** Default number of threads in the Klogging JVM thread pool. */
private const val DEFAULT_KLOGGING_THREAD_COUNT: Int = 10

/**
 * Number of threads in the Klogging JVM pool. Value is:
 *
 * - from environment variable `KLOGGING_COROUTINE_THREADS` if set and parseable as
 *   an [Int];
 * - otherwise [DEFAULT_KLOGGING_THREAD_COUNT].
 *
 * The value has a minimum value of 1.
 */
private val kloggingThreadPoolSize: Int =
    getenvInt(ENV_KLOGGING_COROUTINE_THREADS, DEFAULT_KLOGGING_THREAD_COUNT)

/** Counter of threads created, used in their names. */
internal val threadCount = AtomicInteger(0)

/**
 * Parent [CoroutineContext] used by Klogger running in the JVM.
 *
 * - If the feature flag `KLOGGING_FF_EXECUTOR_THREAD_POOL` evaluates to `true`, create
 *   a fixed thread pool of size [kloggingThreadPoolSize].
 * - If the feature flag `KLOGGING_FF_GLOBAL_SCOPE` evaluates to `true`, use [GlobalScope]
 *   coroutine context.
 * - Otherwise, use the default coroutine dispatcher.
 */
@OptIn(DelicateCoroutinesApi::class)
internal actual fun parentContext(): CoroutineContext =
    if (getenvBoolean(ENV_KLOGGING_FF_EXECUTOR_THREAD_POOL, false)) {
        debug(
            "Coroutines",
            "Creating parent context for Klogging with pool of $kloggingThreadPoolSize threads",
        )
        Executors.newFixedThreadPool(kloggingThreadPoolSize) { r ->
            Thread(r, "klogging-${threadCount.incrementAndGet()}")
        }.asCoroutineDispatcher()
    } else if (getenvBoolean(ENV_KLOGGING_FF_GLOBAL_SCOPE, false)) {
        debug("Coroutines", "Creating parent context for Klogging using GlobalScope")
        GlobalScope.coroutineContext
    } else if (getenvBoolean(ENV_KLOGGING_DISPATCHERS_IO, false)) {
        debug("Coroutines", "Creating parent context for Klogging using Dispatchers.IO")
        Dispatchers.IO
    } else {
        debug("Coroutines", "Creating parent context for Klogging with default dispatcher")
        Job()
    }
