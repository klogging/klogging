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
    get() = "                    ${compressLoggerName(this, 20)}"
        .let { it.substring(it.length - 20) }

public fun compressLoggerName(name: String, width: Int = 20): String {
    if (name.length <= width) return name

    val words = name.split(".")
    val partWords = words.mapIndexed { idx, word ->
        if (idx < (words.size - 1)) word.substring(0, 1) else word
    }
    return partWords.joinToString(".")
}

/**
 * Implementation of [RenderString] like that for Log4J2 with ANSI colouring of
 * level output to a console.
 */
public val RENDER_ANSI: RenderString = { e: LogEvent ->
    val message = "${e.timestamp.localTime} ${e.level.colour5} ${e.logger.right20} :" +
        " ${e.evalTemplate()}"
    val maybeItems = if (e.items.isNotEmpty()) " : ${e.items}" else ""
    val maybeStackTrace = if (e.stackTrace != null) "\n${e.stackTrace}" else ""
    message + maybeItems + maybeStackTrace
}
