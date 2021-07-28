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

import io.klogging.impl.NoCoLoggerImpl
import kotlin.reflect.KClass

private val NOCO_LOGGERS: MutableMap<String, NoCoLogger> = mutableMapOf()

internal fun noCoLoggerFor(name: String?): NoCoLogger {
    val loggerName = name ?: "Klogger"
    return NOCO_LOGGERS.getOrPut(loggerName) { NoCoLoggerImpl(loggerName) }
}

public fun noCoLogger(name: String): NoCoLogger = noCoLoggerFor(name)

public fun noCoLogger(ownerClass: KClass<*>): NoCoLogger = noCoLoggerFor(classNameOf(ownerClass))

public interface NoCoLogging {
    public val logger: NoCoLogger
        get() = noCoLoggerFor(classNameOf(this::class))
}
