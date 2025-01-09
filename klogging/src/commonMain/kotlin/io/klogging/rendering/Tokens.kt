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

/**
 * Set of token classes parsed by [tokenisePattern] function.
 *
 * Each token can potentially have a format specified.
 *
 * Most subclasses can have a width specified as well.
 */
internal sealed class RenderToken(var format: String? = null)

/**
 * Token that represents the starting state.
 */
internal object NoToken : RenderToken()

/**
 * A span of text that is not a recognised token. [StringToken] contains
 * the string that will be output in all log messages.
 */
internal data class StringToken(val value: String) : RenderToken()

/**
 * Render log event timestamp.
 */
internal data class TimestampToken(val width: Int = 0) : RenderToken()

/**
 * Render log event host.
 */
internal data class HostToken(val width: Int = 0) : RenderToken()

/**
 * Render log event logger.
 */
internal data class LoggerToken(val width: Int = 0) : RenderToken()

/**
 * Render log event context, if present.
 */
internal data class ContextToken(val width: Int = 0) : RenderToken()

/**
 * Render log event level.
 */
internal data class LevelToken(val width: Int = 0) : RenderToken()

/**
 * Render log message.
 */
internal data class MessageToken(val width: Int = 0) : RenderToken()

/**
 * Render log stacktrace, if present.
 */
internal data class StacktraceToken(val maxLines: Int = 0) : RenderToken()

/**
 * Render log event items, if any.
 */
internal object ItemsToken : RenderToken()

/**
 * Render a newline.
 */
internal object NewlineToken : RenderToken()

/**
 * Map with keys that are recognised token specifiers to the constructors
 * for the associated classes.
 */
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
