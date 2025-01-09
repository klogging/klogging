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
import kotlin.math.max

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
    get() = this.rpad(5)

internal fun Level.rpad(width: Int): String {
    val padWidth = max(1, width)
    return if (padWidth > name.length)
        (" ".repeat(padWidth) + name).let { it.substring(it.length - padWidth)}
    else
        name.substring(0, padWidth)
}

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

/**
 * Shortens a character sequence and right-pads it if shorter than the specified width.
 */
public fun CharSequence.shortenRight(width: Int): CharSequence = this
    .shortenName(width)
    .padRight(width)

/**
 * Right-pads a character sequence within the specified width if it fits; else the
 * right-most characters of that sequence.
 *
 * @param width number of characters into which to fit this sequence
 * @return right-padded or shortened or the same sequence
 */
public fun CharSequence.padRight(width: Int): CharSequence =
    (" ".repeat(width) + this).let { it.substring(it.length - width) }

private const val delimiters = ". /:-+"

/**
 * Shortens the given name if it exceeds the specified width.
 *
 * @param this@shortenName The name to be shortened.
 * @param width The maximum width of the shortened name; must be at least 5. Defaults to 20.
 * @return The shortened name if it exceeds the width, otherwise the original name.
 */
public fun CharSequence.shortenName(width: Int = DEFAULT_MAX_WIDTH): CharSequence {
    val maxWidth = if (width < MINIMUM_MAX_WIDTH) MINIMUM_MAX_WIDTH else width
    if (length <= maxWidth) return this
    forEachIndexed { idx, char ->
        if (char in delimiters && idx > 0) {
            return substring(0, 1) + char + substring(idx + 1).shortenName(maxWidth - 2)
        }
    }
    return substring(0, maxWidth)
}

/**
 * Extension property that extracts the list of items and stack trace if present in a [LogEvent].
 */
public val LogEvent.itemsAndStackTrace: String
    get() = buildString {
        if (items.isNotEmpty()) {
            append(" : ")
            append(items)
        }
        stackTrace?.let { stackTrace ->
            append("\n")
            append(stackTrace)
        }
    }
