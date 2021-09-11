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

package io.klogging.internal

import io.klogging.config.ENV_FLAG_JVM_USE_EXECUTOR_THREAD_POOL
import io.klogging.config.ENV_KLOGGING_COROUTINE_THREADS
import io.klogging.config.getenvBoolean
import io.klogging.config.getenvInt
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

private const val DEFAULT_KLOGGING_THREAD_COUNT: Int = 10
private val kloggingThreadCount: Int =
    getenvInt(ENV_KLOGGING_COROUTINE_THREADS, DEFAULT_KLOGGING_THREAD_COUNT)

/**
 * Parent coroutine [Job] used by Klogger running in the JVM.
 */
internal actual fun parentContext(): CoroutineContext {
    if (getenvBoolean(ENV_FLAG_JVM_USE_EXECUTOR_THREAD_POOL, false)) {
        debug("Creating parent context for Klogging with pool of $kloggingThreadCount threads")
        return Job() +
            Executors.newFixedThreadPool(kloggingThreadCount).asCoroutineDispatcher()
    } else {
        debug("Creating parent context for Klogging with default dispatcher")
        return Job()
    }
}
