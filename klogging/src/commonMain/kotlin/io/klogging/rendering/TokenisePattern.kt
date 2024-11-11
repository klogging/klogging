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

import io.klogging.internal.warn

internal sealed class RenderToken
internal data class StringToken(val value: String) : RenderToken()
internal data class TimestampToken(val width: Int = 0) : RenderToken()
internal data class HostToken(val width: Int = 0) : RenderToken()
internal data class LoggerToken(val width: Int = 0) : RenderToken()
internal data class ContextToken(val width: Int = 0) : RenderToken()
internal data class LevelToken(val width: Int = 0) : RenderToken()
internal data class MessageToken(val width: Int = 0) : RenderToken()
internal object StacktraceToken : RenderToken()
internal object ItemsToken : RenderToken()
internal object NewlineToken : RenderToken()

private enum class TokeniserState { NONE, IN_STRING, IN_PERCENT }

private val digits = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-')

internal fun tokenisePattern(pattern: String): List<RenderToken> = buildList {
    var state = TokeniserState.NONE
    var width = StringBuilder()
    var tokenString = StringBuilder()

    fun widthToInt(idx: Int): Int {
        if (width.isEmpty()) return 0
        val widthString = width.toString()
        val widthOrNull = widthString.toIntOrNull()
        return if (widthOrNull == null) {
            warn(
                "tokeniser",
                "Pattern \"$pattern\" contains invalid width \"$widthString\" at character ${idx - widthString.length + 1}"
            )
            0
        } else widthOrNull
    }

    fun handleToken(ch: Char, idx: Int, ctor: (Int) -> RenderToken) {
        when (state) {
            TokeniserState.NONE -> {
                tokenString = StringBuilder()
                tokenString.append(ch)
                state = TokeniserState.IN_STRING
            }

            TokeniserState.IN_PERCENT -> {
                add(ctor(widthToInt(idx)))
                width = StringBuilder()
                state = TokeniserState.NONE
            }

            TokeniserState.IN_STRING -> {
                tokenString.append(ch)
            }
        }
    }

    pattern.forEachIndexed { idx, ch ->
        when (ch) {
            '%' -> when (state) {
                TokeniserState.NONE -> state = TokeniserState.IN_PERCENT
                TokeniserState.IN_PERCENT -> {
                    add(StringToken("%"))
                    state = TokeniserState.NONE
                }

                TokeniserState.IN_STRING -> {
                    add(StringToken(tokenString.toString()))
                    state = TokeniserState.IN_PERCENT
                }
            }

            't' -> handleToken(ch, idx) { width -> TimestampToken(width) }

            'h' -> handleToken(ch, idx) { width -> HostToken(width) }

            'l' -> handleToken(ch, idx) { width -> LoggerToken(width) }

            'c' -> handleToken(ch, idx) { width -> ContextToken(width) }

            'v' -> handleToken(ch, idx) { width -> LevelToken(width) }

            's' -> handleToken(ch, idx) { StacktraceToken }

            'i' -> handleToken(ch, idx) { ItemsToken }

            'm' -> handleToken(ch, idx) { width -> MessageToken(width) }

            'n' -> handleToken(ch, idx) { NewlineToken }

            in digits -> when (state) {
                TokeniserState.NONE -> {
                    tokenString = StringBuilder()
                    tokenString.append(ch)
                    state = TokeniserState.IN_STRING
                }

                TokeniserState.IN_PERCENT -> {
                    width.append(ch)
                }

                TokeniserState.IN_STRING -> {
                    tokenString.append(ch)
                }
            }

            else -> when (state) {
                TokeniserState.NONE -> {
                    tokenString = StringBuilder()
                    tokenString.append(ch)
                    state = TokeniserState.IN_STRING
                }

                TokeniserState.IN_PERCENT -> {
                    warn("tokeniser", "Pattern \"$pattern\" contains invalid token \"%$ch\" at character $idx")
                    state = TokeniserState.NONE
                }

                TokeniserState.IN_STRING -> {
                    tokenString.append(ch)
                }
            }
        }
    }
    if (state == TokeniserState.IN_STRING) {
        add(StringToken(tokenString.toString()))
    }
}
