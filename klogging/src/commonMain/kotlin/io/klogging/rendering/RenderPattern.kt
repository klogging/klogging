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
import io.klogging.events.LogEvent

/**
 * [RenderString] implementation that interprets a string pattern of
 * tokens specifying log event fields to show.
 *
 * Pattern syntax is modelled after pattern layouts from Log4j and Logback.
 *
 * Token format is `%[WIDTH]X[{FORMAT}]` where:
 * - WIDTH is an optional integer width. If the field is wider than the specified width,
 *   it is truncated to that width. If the field is narrower, it is padded with spaces
 *   to that width: left-aligned if width is positive and right-aligned if it is negative.
 * - X specifies the log event field to render:
 *   - `t` -> [LogEvent.timestamp] in ISO8601 format.
 *     Use the format LOCAL_TIME to output local time.
 *   - `h` -> [LogEvent.host].
 *   - `l` -> [LogEvent.logger].
 *   - `c` -> [LogEvent.context], if present.
 *   - `v` -> [LogEvent.level]. Use the format COLOUR or COLOR to output different colours
 *     for different levels.
 *   - `m` -> [LogEvent.message] after evaluating it as a message template.
 *   - `s` -> [LogEvent.stackTrace] if present, preceded by a newline. If WIDTH is specified,
 *     limit the number of lines output.
 *   - `i` -> [LogEvent.items], if there are any.
 *   - `n` -> output a newline.
 */
public class RenderPattern(
    private val pattern: String = "%m",
) : RenderString {
    override fun invoke(event: LogEvent): String = buildString {
        val tokens = tokenisePattern(pattern)
        tokens.forEach { token ->
            when (token) {
                is NoToken -> append("")
                is StringToken -> append(token.value)
                is TimestampToken -> append(token.render(event))
                is HostToken -> append(token.render(event))
                is LoggerToken -> append(token.render(event))
                is ContextToken -> append(token.render(event))
                is LevelToken -> append(token.render(event))
                is MessageToken -> append(event.evalTemplate())
                is StacktraceToken -> append(token.render(event))
                is ItemsToken -> if (event.items.isNotEmpty()) append(event.items)
                is NewlineToken -> append("\n")
            }
        }
    }
}

private fun String.padLeft(width: Int): String =
    (this + " ".repeat(width)).substring(0, width)

private fun String.padRight(width: Int): String = when {
    width > length -> " ".repeat(width - length) + this
    width in 0..length -> this.substring(0, width)
    else -> this
}

private fun String.shortenAndPad(width: Int) = when {
    width > 0 -> shortenName(width).toString().padLeft(width)
    width < 0 -> shortenName(-width).toString().padRight(-width)
    else -> this
}

private fun LevelToken.render(event: LogEvent): String {
    val string = event.level.toString().shortenAndPad(width)
    return when (format) {
        in setOf("COLOUR", "COLOR") -> when (event.level) {
            Level.TRACE -> grey(string)
            Level.INFO -> green(string)
            Level.WARN -> yellow(string)
            Level.ERROR -> red(string)
            Level.FATAL -> brightRed(string)
            else -> string
        }

        else -> string
    }
}

private fun TimestampToken.render(event: LogEvent): String {
    val string = when (format) {
        "LOCAL_TIME" -> event.timestamp.localTime
        else -> event.timestamp.toString()
    }
    return when {
        width > 0 -> string.padLeft(width)
        width < 0 -> string.padRight(-width)
        else -> string
    }
}

private fun HostToken.render(event: LogEvent): String =
    event.host.shortenAndPad(width)

private fun ContextToken.render(event: LogEvent): String =
    event.context?.shortenAndPad(width) ?: ""

private fun LoggerToken.render(event: LogEvent): String =
    event.logger.shortenAndPad(width)

private fun StacktraceToken.render(event: LogEvent): String = event.stackTrace?.let {
    val trace = if (maxLines > 0) {
        it.split('\n').take(maxLines).joinToString("\n")
    } else it
    "\n$trace"
} ?: ""
