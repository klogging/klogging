/*

   Copyright 2021-2024 Michael Strasser.

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

public class RenderPattern(
    private val pattern: String = "%m%n",
) : RenderString {
    override fun invoke(event: LogEvent): String = buildString {
        val tokens = tokenisePattern(pattern)
        tokens.forEach { token ->
            when (token) {
                is NoToken -> append("")
                is StringToken -> append(token.value)
                is TimestampToken -> append(token.render(event))
                is HostToken -> append(event.host)
                is LoggerToken -> append(event.logger)
                is ContextToken -> append(event.context)
                is LevelToken -> append(token.render(event))
                is MessageToken -> append(event.message)
                is StacktraceToken -> append(event.stackTrace)
                is ItemsToken -> append(event.items)
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

private fun LevelToken.render(event: LogEvent): String {
    val string = when {
        width > 0 -> event.level.toString().padLeft(width)
        width < 0 -> event.level.toString().padRight(-width)
        else -> event.level.toString()
    }
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
