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

internal sealed class RenderToken(var format: String? = null)
internal object NoToken : RenderToken()
internal data class StringToken(val value: String) : RenderToken()
internal data class TimestampToken(val width: Int = 0) : RenderToken()
internal data class HostToken(val width: Int = 0) : RenderToken()
internal data class LoggerToken(val width: Int = 0) : RenderToken()
internal data class ContextToken(val width: Int = 0) : RenderToken()
internal data class LevelToken(val width: Int = 0) : RenderToken()
internal data class MessageToken(val width: Int = 0) : RenderToken()
internal data class StacktraceToken(val maxLines: Int = 0) : RenderToken()
internal object ItemsToken : RenderToken()
internal object NewlineToken : RenderToken()

internal val tokens = mapOf<Char, (Int) -> RenderToken>(
    't' to { TimestampToken(it) },
    'h' to { HostToken(it) },
    'l' to { LoggerToken(it) },
    'c' to { ContextToken(it) },
    'v' to { LevelToken(it) },
    'm' to { MessageToken(it) },
    's' to { StacktraceToken(it) },
    'i' to { ItemsToken },
    'n' to { NewlineToken },
)
