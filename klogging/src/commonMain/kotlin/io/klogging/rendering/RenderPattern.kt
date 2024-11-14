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

import io.klogging.events.LogEvent

public class RenderPattern(
    private val pattern: String = "%m%n",
) : RenderString {
    override fun invoke(event: LogEvent): String = buildString {
        val tokens = tokenisePattern(pattern)
        tokens.forEach { token ->
            when (token) {
                is FormatToken -> append(token.value)
                is StringToken -> append(token.value)
                is TimestampToken -> append(event.timestamp)
                is HostToken -> append(event.host)
                is LoggerToken -> append(event.logger)
                is ContextToken -> append(event.context)
                is LevelToken -> append(event.level)
                is MessageToken -> append(event.message)
                is StacktraceToken -> append(event.stackTrace)
                is ItemsToken -> append(event.items)
                is NewlineToken -> append("\n")
            }
        }
    }
}


