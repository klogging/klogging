/*

   Copyright 2021-2022 Michael Strasser.

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

package io.klogging.events

import io.klogging.Level
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.random.nextUInt

public typealias EventItems = Map<String, Any?>

/** An event at a point in time with information about the running state of a program. */
public data class LogEvent(
    /** Unique identifier for this event. */
    val id: String = randomId(),
    /** When the event occurred, to microsecond or better precision. */
    val timestamp: Instant = timestampNow(),
    /** Host where the event occurred. */
    val host: String = hostname,
    /** Name of the logger that emitted the event. */
    val logger: String,
    /** Name of the thread or similar context identifier where the event was emitted. */
    val context: String? = currentContext(),
    /** Severity [Level] of the event. */
    val level: Level,
    /** [Message template](https://messagetemplates.org), if any, used to construct the message. */
    val template: String? = null,
    /** Message describing the event. */
    val message: String,
    /**
     *  String stack trace information that may be included if an error or exception is
     *  associated with the event.
     */
    val stackTrace: String? = null,
    /**
     * Map of items current at the time of the event, to be displayed as structured data.
     *
     * If the message string was constructed from a template, there is one item per
     * hole in the template.
     */
    val items: EventItems = mapOf()
) {
    /**
     * Copy this [LogEvent], setting the level, the stack trace from any error or exception, and
     * context items.
     *
     * This function is used when an event has already been constructed.
     */
    internal fun copyWith(
        newLevel: Level,
        newStacktrace: String? = null,
        contextItems: EventItems = mapOf()
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
        items = contextItems + items
    )

    /**
     * Add context items to an event, returning a new event.
     */
    internal fun addContext(contextItems: EventItems) =
        copyWith(this.level, this.stackTrace, contextItems)
}

/**
 * Random ID for a [LogEvent].
 *
 * This implementation generates identifiers that are _compact_ for when they
 * are printed by the internal logger and random enough (64 bits).
 */
private fun randomId(): String = Random.nextUInt().toString(16)

/** Name of the executing host, included in all log events. */
public expect val hostname: String

/** Thread name or similar current context identifier. */
internal expect fun currentContext(): String?
