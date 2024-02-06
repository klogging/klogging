/*

   Copyright 2021-2024 Michael Strasser.

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

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import kotlinx.datetime.Instant

internal const val MINIMUM_MAX_WIDTH = 5
internal const val DEFAULT_MAX_WIDTH = 20

/** Only the local time component. */
public val Instant.localTime: String
    get() = localString.substring(11)

// ANSI colour escapes
private const val ESC = "\u001B["
public fun grey(str: String): String = "${ESC}90m$str${ESC}0m"
public fun green(str: String): String = "${ESC}32m$str${ESC}0m"
public fun yellow(str: String): String = "${ESC}33m$str${ESC}0m"
public fun red(str: String): String = "${ESC}31m$str${ESC}0m"
public fun brightRed(str: String): String = "${ESC}91m$str${ESC}0m"

// Right-aligned level name in 5 character space.
private val Level.rpad5: String
    get() = " $name".let { it.substring(it.length - 5) }

/** Render a level in colour using ANSI colour escapes. */
public val Level.colour5: String
    get() = when (this) {
        TRACE -> grey(rpad5)
        DEBUG -> rpad5
        INFO -> green(rpad5)
        WARN -> yellow(rpad5)
        ERROR -> red(rpad5)
        FATAL -> brightRed(rpad5)
        else -> rpad5
    }

public val String.right20: String
    get() = "                    ${shortenName(this, DEFAULT_MAX_WIDTH)}"
        .let { it.substring(it.length - DEFAULT_MAX_WIDTH) }

private const val delimiters = ". /:-+"

/**
 * Shortens the given name if it exceeds the specified width.
 *
 * @param name The name to be shortened.
 * @param width The maximum width of the shortened name; must be at least 5. Defaults to 20.
 * @return The shortened name if it exceeds the width, otherwise the original name.
 */
public fun shortenName(name: CharSequence, width: Int = DEFAULT_MAX_WIDTH): CharSequence {
    val maxWidth = if (width < MINIMUM_MAX_WIDTH) DEFAULT_MAX_WIDTH else width
    if (name.length <= maxWidth) return name
    name.forEachIndexed { idx, char ->
        if (char in delimiters && idx > 0) {
            return name.substring(0, 1) + char + shortenName(name.substring(idx + 1), maxWidth - 2)
        }
    }
    return name.substring(0, maxWidth)
}

/**
 * Implementation of [RenderString] like that for Log4J2 with ANSI colouring of
 * level output to a console.
 */
public val RENDER_ANSI: RenderString = object : RenderString {
    override fun invoke(event: LogEvent): String {
        val message = "${event.timestamp.localTime} ${event.level.colour5} [${event.context?.right20}] :" +
                " ${event.logger.right20} : ${event.evalTemplate()}"
        val maybeItems = if (event.items.isNotEmpty()) " : ${event.items}" else ""
        val maybeStackTrace = if (event.stackTrace != null) "\n${event.stackTrace}" else ""
        return message + maybeItems + maybeStackTrace
    }
}
