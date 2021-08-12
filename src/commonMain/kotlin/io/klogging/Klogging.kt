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

package io.klogging

import io.klogging.impl.KloggerImpl
import io.klogging.internal.KloggingState
import kotlin.reflect.KClass

/** Get the name of a class. */
public expect fun classNameOf(ownerClass: KClass<*>): String?

/**
 * Runtime list of current [Klogger] instances.
 */
private val LOGGERS: MutableMap<String, Klogger> = mutableMapOf()

/**
 * Returns a [Klogger] for the specified name: returning an existing one
 * or creating a new one if needed.
 */
internal fun loggerFor(name: String?): Klogger {
    // This property is lazily set by checking for a JSON configuration file.
    KloggingState.configuration
    val loggerName = name ?: "Klogging"
    return LOGGERS.getOrPut(loggerName) { KloggerImpl(loggerName) }
}

/** Returns a [Klogger] with the specified name. */
public fun logger(name: String): Klogger = loggerFor(name)

/** Returns a [Klogger] with the name of the specified class. */
public fun logger(ownerClass: KClass<*>): Klogger = loggerFor(classNameOf(ownerClass))

/**
 * Utility interface that supplies a [Klogger] property called `logger`.
 */
public interface Klogging {
    public val logger: Klogger
        get() = loggerFor(classNameOf(this::class))
}
