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

package io.klogging.impl

import io.klogging.BaseLogger
import io.klogging.Level
import io.klogging.events.LogEvent
import io.klogging.events.currentContext

/**
 * Copy a [LogEvent], setting the level, the stack trace from any exception, and
 * context items.
 *
 * This function is used when an event has already been constructed, for example
 * by the [Klogger#e] and [NoCoLogger#e] functions.
 */
internal fun LogEvent.copyWith(
    newLevel: Level,
    newStacktrace: String? = null,
    contextItems: Map<String, Any?> = mapOf()
): LogEvent = LogEvent(
    id = id,
    timestamp = timestamp,
    host = host,
    logger = logger,
    context = context ?: currentContext(),
    level = newLevel,
    template = template,
    message = message,
    stackTrace = newStacktrace,
    items = contextItems + items,
)

/**
 * Extension function on [BaseLogger] that constructs a [LogEvent] from a range of types.
 *
 * - If the object is an event already, update it with level, stack trace (if present)
 *   and context items.
 * - Otherwise, construct an event with supplied information.
 */
internal fun BaseLogger.eventFrom(
    level: Level,
    exception: Exception?,
    eventObject: Any?,
    contextItems: Map<String, Any?> = mapOf(),
): LogEvent {
    return when (eventObject) {
        is LogEvent ->
            eventObject.copyWith(level, exception?.stackTraceToString(), contextItems)
        else -> {
            val (message, stackTrace) = messageAndStackTrace(eventObject, exception)
            LogEvent(
                logger = this.name,
                level = level,
                message = message,
                stackTrace = stackTrace,
                items = contextItems,
            )
        }
    }
}

/**
 * Extract message and stack trace values from a non-[LogEvent] object.
 *
 * @param obj an object that has been sent in a logging function call.
 *
 * @param exception an exception that may have been sent in a logging function call.
 *
 * @return a pair with the message to show and any stack trace that is present:
 *   - If the object is an exception, return its message and stack trace.
 *   - If the object is not an exception, return `toString()` on the object
 *     and any stack trace on the supplied exception.
 */
internal fun messageAndStackTrace(obj: Any?, exception: Exception?): Pair<String, String?> =
    when (obj) {
        is Exception -> (obj.message ?: "Exception") to obj.stackTraceToString()
        else -> obj.toString() to exception?.stackTraceToString()
    }
