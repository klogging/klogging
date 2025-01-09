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
import io.klogging.impl.NoCoLoggerImpl
import io.klogging.internal.KloggingEngine
import io.klogging.internal.trace
import kotlin.reflect.KClass

/**
 * Returns a [NoCoLogger] for the specified name: returning an existing one
 * or creating a new one if needed.
 */
internal fun noCoLoggerFor(
    name: String?,
    vararg loggerContextItems: ContextItem,
    otherLogger: BaseLogger? = null,
): NoCoLogger {
    // Ensure file configuration has been loaded
    KloggingEngine.configuration
    val loggerName = name ?: "NoCoLogger"
    val contextItems = (otherLogger?.loggerContextItems ?: emptyMap()) + mapOf(*loggerContextItems)
    trace("NoCoLogging", "Adding NoCoLogger $loggerName")
    return NoCoLoggerImpl(loggerName, contextItems)
}

/** Returns a [NoCoLogger] with the specified name. */
public fun noCoLogger(
    name: String,
    vararg loggerContextItems: ContextItem,
): NoCoLogger = noCoLoggerFor(name, *loggerContextItems)

public fun noCoLogger(
    name: String,
    otherLogger: BaseLogger?,
    vararg loggerContextItems: ContextItem,
): NoCoLogger = noCoLoggerFor(name, *loggerContextItems, otherLogger = otherLogger)

/** Returns a [NoCoLogger] with the name of the specified class. */
public fun noCoLogger(
    ownerClass: KClass<*>,
    vararg loggerContextItems: ContextItem,
): NoCoLogger = noCoLoggerFor(classNameOf(ownerClass), *loggerContextItems)

public fun noCoLogger(
    ownerClass: KClass<*>,
    otherLogger: BaseLogger?,
    vararg loggerContextItems: ContextItem,
): NoCoLogger = noCoLoggerFor(classNameOf(ownerClass), *loggerContextItems, otherLogger = otherLogger)

/** Returns a [NoCoLogger] with the name of the specified class. */
public inline fun <reified T> noCoLogger(
    vararg loggerContextItems: ContextItem,
): NoCoLogger =
    noCoLogger(T::class, *loggerContextItems)

public inline fun <reified T> noCoLogger(
    otherLogger: BaseLogger?,
    vararg loggerContextItems: ContextItem,
): NoCoLogger =
    noCoLogger(T::class, otherLogger, *loggerContextItems)

/**
 * Utility interface that supplies a [NoCoLogger] property called `logger`.
 */
public interface NoCoLogging {
    public val logger: NoCoLogger
        get() = noCoLoggerFor(classNameOf(this::class))
}
