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

package io.klogging.rendering

import io.klogging.events.LogEvent
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Extension property for Kotlin [Instant] to render it in local time as two words.
 *
 * The simple mechanism is to render as ISO8601 and replace the `T` with a space.
 */
public val Instant.localString: String
    get() = toLocalDateTime(TimeZone.currentSystemDefault())
        .toString()
        .replace('T', ' ')

/**
 * Simple implementation of [RenderString] for output to the standard output stream, mostly on one
 * line.
 *
 * If there is a stack trace it is on second and following lines.
 */
public val RENDER_SIMPLE: RenderString = { e: LogEvent ->
    val message = "${e.timestamp.localString} ${e.level} [${e.context}] ${e.logger} : ${e.evalTemplate()}"
    val maybeItems = if (e.items.isNotEmpty()) " : ${e.items}" else ""
    val maybeStackTrace = if (e.stackTrace != null) "\n${e.stackTrace}" else ""
    message + maybeItems + maybeStackTrace
}

public fun LogEvent.evalTemplate(): String = items.entries
    .filter { entry -> entry.value != null }
    .fold(message) { message, (key, value) -> message.replace("{$key}", value.toString()) }
