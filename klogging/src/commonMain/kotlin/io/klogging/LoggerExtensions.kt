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

/**
 * Creates a [NoCoLogger] from this [Klogger] using the same name.
 *
 * @param loggerContextItems zero or more pairs of context items
 */
public fun Klogger.toNoCoLogger(vararg loggerContextItems: ContextItem): NoCoLogger =
    noCoLogger(this.name, this, *loggerContextItems)

/**
 * Creates a [NoCoLogger] from this [Klogger] using a different name.
 *
 * @param name Name of the new logger
 * @param loggerContextItems zero or more pairs of context items
 */
public fun Klogger.toNoCoLogger(
    name: String,
    vararg loggerContextItems: ContextItem,
): NoCoLogger = noCoLogger(name, this, *loggerContextItems)

/**
 * Creates a [Klogger] from this [NoCoLogger] using the same name.
 *
 * @param loggerContextItems zero or more pairs of context items
 */
public fun NoCoLogger.toKlogger(vararg loggerContextItems: ContextItem): Klogger =
    logger(this.name, this, *loggerContextItems)

/**
 * Creates a [Klogger] from this [NoCoLogger] using a different name.
 *
 * @param name Name of the new logger
 * @param loggerContextItems zero or more pairs of context items
 */
public fun NoCoLogger.toKlogger(
    name: String,
    vararg loggerContextItems: ContextItem,
): Klogger = logger(name, this, *loggerContextItems)
