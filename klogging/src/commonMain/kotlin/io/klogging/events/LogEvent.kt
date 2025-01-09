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

package io.klogging.events

import io.klogging.Level
import kotlinx.coroutines.CoroutineName
import kotlinx.datetime.Instant
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.random.nextUInt

public typealias EventItems = Map<String, Any?>

/**
 * An event at a point in time with information about the running state of a program.
 *
 * @property id Unique identifier for this event
 * @property timestamp When the event occurred, to microsecond or better precision
 * @property host Host where the event occurred
 * @property logger Name of the logger that emitted the event
 * @property context Name of the thread or similar context identifier where the event was emitted
 * @property level Severity [Level] of the event
 * @property template [Message template](https://messagetemplates.org), if any, used to construct the message
 * @property message Message describing the event
 * @property stackTrace String stack trace information that may be included if an error or exception is
 *                      associated with the event
 * @property items Map of items current at the time of the event, to be displayed as structured data.
 *                 If the message string was constructed from a template, there is one item per
 *                 hole in the template
 */
public data class LogEvent(
    val id: String = randomId(),
    val timestamp: Instant = timestampNow(),
    val host: String = hostname,
    val logger: String,
    val context: String? = threadContext(),
    val level: Level,
    val template: String? = null,
    val message: String,
    val stackTrace: String? = null,
    val items: EventItems = mapOf(),
) {
    /**
     * Copy this [LogEvent], setting the level, the stack trace from any error or exception, and
     * context items.
     *
     * This function is used when an event has already been constructed.
     *
     * @param newLevel level of the new event
     * @param newStacktrace new stack trace information, if any
     * @param contextItems context items to add to those already in the event being copied
     * @return a new [LogEvent] with new information as specified
     */
    internal fun copyWith(
        newLevel: Level,
        newStacktrace: String? = null,
        contextItems: EventItems = mapOf(),
    ): LogEvent = LogEvent(
        id = id,
        timestamp = timestamp,
        host = host,
        logger = logger,
        context = context ?: threadContext(),
        level = newLevel,
        template = template,
        message = message,
        stackTrace = newStacktrace,
        items = contextItems + items,
    )

    /**
     * Add context items to an event, returning a new event.
     * @param contextItems context items to add to an event
     * @return a new [LogEvent] with added context items
     */
    internal fun addContext(contextItems: EventItems) =
        copyWith(this.level, this.stackTrace, contextItems)
}

/**
 * Random ID for a [LogEvent].
 *
 * This implementation generates identifiers that are _compact_ for when they
 * are printed by the internal logger and random enough (64 bits).
 *
 * @return a short, random string
 */
private fun randomId(): String = Random.nextUInt().toString(16)

/** Name of the executing host, included in all log events. */
internal expect val hostname: String

/** Thread name or similar current context identifier. */
internal expect fun threadContext(): String?

/**
 * Construct a context name from thread context and coroutine name, if set.
 * @return name to use for the context
 */
internal suspend fun contextName(): String =
    listOfNotNull(threadContext(), coroutineContext[CoroutineName]?.name).joinToString("+")
