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

package io.klogging

import io.klogging.impl.NoCoLoggerImpl
import io.klogging.internal.KloggingEngine
import io.klogging.internal.trace
import kotlin.reflect.KClass

/**
 * Runtime list of current [NoCoLogger] instances.
 */
private val NOCO_LOGGERS: MutableMap<String, NoCoLogger> = AtomicMutableMap()

/**
 * Returns a [NoCoLogger] for the specified name: returning an existing one
 * or creating a new one if needed.
 */
internal fun noCoLoggerFor(name: String?): NoCoLogger {
    // This property is lazily set by checking for a JSON configuration file.
    // TODO: Can we ensure this is not optimised out of the code?
    KloggingEngine.configuration
    val loggerName = name ?: "NoCoLogger"
    return NOCO_LOGGERS.getOrPut(loggerName) {
        trace("NoCoLogging", "Adding NoCoLogger $loggerName")
        NoCoLoggerImpl(loggerName)
    }
}

/** Returns a [NoCoLogger] with the specified name. */
public fun noCoLogger(name: String): NoCoLogger = noCoLoggerFor(name)

/** Returns a [NoCoLogger] with the name of the specified class. */
public fun noCoLogger(ownerClass: KClass<*>): NoCoLogger = noCoLoggerFor(classNameOf(ownerClass))

/** Returns a [NoCoLogger] with the name of the specified class. */
public inline fun <reified T> noCoLogger(): NoCoLogger = noCoLogger(T::class)

/**
 * Utility interface that supplies a [NoCoLogger] property called `logger`.
 */
public interface NoCoLogging {
    public val logger: NoCoLogger
        get() = noCoLoggerFor(classNameOf(this::class))
}
