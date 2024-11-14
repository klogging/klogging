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

private enum class TokeniserState { NONE, IN_STRING, IN_PERCENT, IN_FORMAT }

private val digits = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-')

internal fun tokenisePattern(pattern: String): List<RenderToken> = buildList {
    var state = TokeniserState.NONE
    var previousToken: RenderToken = NoToken
    var previousState = TokeniserState.NONE
    var tokenWidth = StringBuilder()
    var tokenString = StringBuilder()

    fun addToken(token: RenderToken) {
        previousToken = token
        add(token)
    }

    fun setState(newState: TokeniserState) {
        previousState = state
        state = newState
    }

    fun tokenWidthAsInt(charPos: Int): Int {
        if (tokenWidth.isEmpty()) return 0
        val widthString = tokenWidth.toString()
        val widthOrNull = widthString.toIntOrNull()
        return if (widthOrNull == null) {
            warn(
                "tokeniser",
                "Pattern \"$pattern\" contains invalid width \"$widthString\" at character ${charPos - widthString.length}"
            )
            0
        } else widthOrNull
    }

    fun createToken(ch: Char, idx: Int) {
        when (state) {
            TokeniserState.NONE -> {
                tokenString = StringBuilder()
                tokenString.append(ch)
                setState(TokeniserState.IN_STRING)
            }

            TokeniserState.IN_PERCENT -> {
                tokens[ch]?.let { creator ->
                    addToken(creator(tokenWidthAsInt(idx + 1)))
                }
                tokenWidth = StringBuilder()
                setState(TokeniserState.NONE)
            }

            TokeniserState.IN_STRING -> {
                tokenString.append(ch)
            }

            TokeniserState.IN_FORMAT -> {
                tokenString.append(ch)
            }
        }
    }


    pattern.forEachIndexed { idx, ch ->
        when (ch) {
            '%' -> when (state) {
                TokeniserState.NONE -> setState(TokeniserState.IN_PERCENT)
                TokeniserState.IN_PERCENT -> {
                    addToken(StringToken("%"))
                    setState(TokeniserState.NONE)
                }

                TokeniserState.IN_STRING -> {
                    addToken(StringToken(tokenString.toString()))
                    setState(TokeniserState.IN_PERCENT)
                }

                TokeniserState.IN_FORMAT -> {
                    tokenString.append(ch)
                }
            }

            in tokens.keys -> createToken(ch, idx)

            in digits -> when (state) {
                TokeniserState.NONE -> {
                    tokenString = StringBuilder()
                    tokenString.append(ch)
                    setState(TokeniserState.IN_STRING)
                }

                TokeniserState.IN_PERCENT -> {
                    tokenWidth.append(ch)
                }

                TokeniserState.IN_STRING -> {
                    tokenString.append(ch)
                }

                TokeniserState.IN_FORMAT -> {
                    tokenString.append(ch)
                }
            }

            else -> when (state) {
                TokeniserState.NONE -> {
                    tokenString = StringBuilder()
                    if (ch == '{' && previousState == TokeniserState.IN_PERCENT) {
                        setState(TokeniserState.IN_FORMAT)
                    } else {
                        tokenString.append(ch)
                        setState(TokeniserState.IN_STRING)
                    }
                }

                TokeniserState.IN_PERCENT -> {
                    warn("tokeniser", "Pattern \"$pattern\" contains invalid token \"%$ch\" at character $idx")
                    setState(TokeniserState.NONE)
                }

                TokeniserState.IN_STRING -> {
                    tokenString.append(ch)
                }

                TokeniserState.IN_FORMAT -> {
                    when (ch) {
                        '}' -> {
                            if (previousToken != NoToken) {
                                previousToken.format = tokenString.toString()
                            }
                            setState(TokeniserState.NONE)
                        }

                        else -> tokenString.append(ch)
                    }
                }
            }
        }
    }
    if (state == TokeniserState.IN_STRING || state == TokeniserState.IN_FORMAT) {
        addToken(StringToken(tokenString.toString()))
    }
}
