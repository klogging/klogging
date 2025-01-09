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

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.klogging.internal.KloggingEngine

/**
 * Base interface of [Klogger] interface for use in coroutines, and
 * [NoCoLogger] interface when not using coroutines.
 */
public interface BaseLogger {

    /** Name of the logger: usually a class name in Java. */
    public val name: String

    /** Context items belonging to the logger. */
    public val loggerContextItems: EventItems

    /**
     * Minimum level at which to emit log events, determined from current
     * configuration.
     */
    public fun minLevel(): Level = KloggingEngine.minimumLevelOf(name)

    /**
     * Check whether this logger will emit log events at the specified logging
     * level.
     *
     * @param level level at which to check
     * @return `true` if logger will emit events at the specified level
     */
    public fun isLevelEnabled(level: Level): Boolean = when (level) {
        NONE -> false
        else -> minLevel() <= level
    }

    /** Is this logger enabled to emit [TRACE] events? */
    public fun isTraceEnabled(): Boolean = isLevelEnabled(TRACE)

    /** Is this logger enabled to emit [DEBUG] events? */
    public fun isDebugEnabled(): Boolean = isLevelEnabled(DEBUG)

    /** Is this logger enabled to emit [INFO] events? */
    public fun isInfoEnabled(): Boolean = isLevelEnabled(INFO)

    /** Is this logger enabled to emit [WARN] events? */
    public fun isWarnEnabled(): Boolean = isLevelEnabled(WARN)

    /** Is this logger enabled to emit [ERROR] events? */
    public fun isErrorEnabled(): Boolean = isLevelEnabled(ERROR)

    /** Is this logger enabled to emit [FATAL] events? */
    public fun isFatalEnabled(): Boolean = isLevelEnabled(FATAL)

    /**
     * Construct a [LogEvent] from a range of types.
     *
     * - If the object is an event already, update it with level, stack trace (if present)
     *   and context items.
     * - Otherwise, construct an event with supplied information.
     *
     * @param context optional context for the event
     * @param level logging level for the event
     * @param throwable optional [Throwable] associated with the event
     * @param eventObject optional object that might already be a [LogEvent]
     * @param contextItems map of context items to include in the new event
     *
     * @return a new [LogEvent] from specified components
     */
    public fun eventFrom(
        context: String? = null,
        level: Level,
        throwable: Throwable? = null,
        eventObject: Any? = null,
        contextItems: EventItems = mapOf(),
    ): LogEvent {
        return when (eventObject) {
            is LogEvent ->
                eventObject.copyWith(level, throwable?.stackTraceToString(), contextItems)

            else -> {
                val (message, stackTrace) = messageAndStackTrace(eventObject, throwable)
                LogEvent(
                    logger = this.name,
                    context = context,
                    level = level,
                    message = message,
                    stackTrace = stackTrace,
                    items = contextItems,
                )
            }
        }
    }
}

/**
 * Extract message and stack trace values from a non-[LogEvent] object.
 *
 * @param obj an object that has been sent in a logging function call.
 *
 * @param throwable an error or exception that may have been sent in a logging function call.
 *
 * @return a pair with the message to show and any stack trace that is present:
 *   - If the object is a throwable, return its message and stack trace.
 *   - If the object is not a throwable, return `toString()` on the object
 *     and any stack trace on the supplied throwable.
 */
internal fun messageAndStackTrace(obj: Any?, throwable: Throwable?): Pair<String, String?> =
    when (obj) {
        is Throwable -> (obj.message ?: "Throwable") to obj.stackTraceToString()
        else -> obj.toString() to throwable?.stackTraceToString()
    }
