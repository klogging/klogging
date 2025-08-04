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

package io.klogging

import io.klogging.context.ContextItem
import io.klogging.context.LogContext
import io.klogging.impl.KloggerImpl
import io.klogging.impl.NoCoLoggerImpl
import kotlin.coroutines.coroutineContext

/**
 * Creates a [NoCoLogger] from this [Klogger] using the same name.
 *
 * @param loggerContextItems zero or more pairs of context items
 */
public fun Klogger.toNoCoLogger(vararg loggerContextItems: ContextItem): NoCoLogger =
    NoCoLoggerImpl(this.name, mapOf(*loggerContextItems) + this.loggerContextItems)

/**
 * Creates a [NoCoLogger] from this [Klogger] using a different name.
 *
 * @param name Name of the new logger
 * @param loggerContextItems zero or more pairs of context items
 */
public fun Klogger.toNoCoLogger(
    name: String,
    vararg loggerContextItems: ContextItem,
): NoCoLogger = NoCoLoggerImpl(name, mapOf(*loggerContextItems) + this.loggerContextItems)

/**
 * Creates a [NoCoLogger] from this [Klogger] using the same name and including any [LogContext] items
 * in current coroutine scope.
 *
 * @param loggerContextItems zero or more pairs of context items
 */
public suspend fun Klogger.toNoCoLoggerWithScopeContext(vararg loggerContextItems: ContextItem): NoCoLogger =
    NoCoLoggerImpl(
        this.name,
        mapOf(*loggerContextItems) + (coroutineContext[LogContext]?.getAll() ?: emptyMap()) + this.loggerContextItems,
    )

/**
 * Creates a [NoCoLogger] from this [Klogger] using a different name and including any [LogContext] items
 *  * in current coroutine scope.
 *
 * @param name Name of the new logger
 * @param loggerContextItems zero or more pairs of context items
 */
public suspend fun Klogger.toNoCoLoggerWithScopeContext(
    name: String,
    vararg loggerContextItems: ContextItem,
): NoCoLogger =
    NoCoLoggerImpl(
        name,
        mapOf(*loggerContextItems) + (coroutineContext[LogContext]?.getAll() ?: emptyMap()) + this.loggerContextItems,
    )

/**
 * Creates a [Klogger] from this [NoCoLogger] using the same name.
 *
 * @param loggerContextItems zero or more pairs of context items
 */
public fun NoCoLogger.toKlogger(vararg loggerContextItems: ContextItem): Klogger =
    KloggerImpl(this.name, mapOf(*loggerContextItems) + this.loggerContextItems)

/**
 * Creates a [Klogger] from this [NoCoLogger] using a different name.
 *
 * @param name Name of the new logger
 * @param loggerContextItems zero or more pairs of context items
 */
public fun NoCoLogger.toKlogger(
    name: String,
    vararg loggerContextItems: ContextItem,
): Klogger = KloggerImpl(name, mapOf(*loggerContextItems) + this.loggerContextItems)
