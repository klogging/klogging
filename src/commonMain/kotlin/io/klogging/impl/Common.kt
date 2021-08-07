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
import kotlinx.datetime.Clock

/**
 * Copy a [LogEvent], setting the level and the stack trace from any exception.
 *
 * This function is used when an event has already been constructed, for example
 * by the [Klogger#e] and [NoCoLogger#e] functions.
 */
private fun LogEvent.copyWith(newLevel: Level, newStacktrace: String?): LogEvent = LogEvent(
    timestamp, host, logger, context ?: currentContext(), newLevel, template, message, newStacktrace, items
)

/**
 * Extension function on [BaseLogger] that constructs a [LogEvent] from a range of types.
 *
 * - If the object is an event already, update it with level and exception (if present).
 * - Otherwise, construct an event with current context and timestamp.
 */
public fun BaseLogger.eventFrom(
    level: Level,
    exception: Exception?,
    eventObject: Any?,
    withItems: Map<String, Any?> = mapOf(),
): LogEvent {
    return when (eventObject) {
        is LogEvent ->
            eventObject.copyWith(level, exception?.stackTraceToString())
        else -> {
            val (message, stackTrace) = messageAndStackTrace(eventObject, exception)
            LogEvent(
                timestamp = Clock.System.now(),
                logger = this.name,
                context = currentContext(),
                level = level,
                message = message,
                stackTrace = stackTrace,
                items = withItems,
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
internal fun messageAndStackTrace(obj: Any?, exception: Exception?): Pair<String, String?> = when (obj) {
    is Exception -> (obj.message ?: "Exception") to obj.stackTraceToString()
    else -> obj.toString() to exception?.stackTraceToString()
}
