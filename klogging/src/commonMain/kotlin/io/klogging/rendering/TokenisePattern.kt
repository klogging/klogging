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

import io.klogging.internal.warn

/**
 * States of the simple state machine.
 */
private enum class TokeniserState { NONE, IN_STRING, IN_PERCENT, IN_FORMAT }

/**
 * Characters that contribute to token widths.
 */
private val digits = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-')

/**
 * Parse a rendering pattern string into tokens using a simple state machine.
 *
 * @param pattern string that may contain recogised tokens
 * @return list of tokens parsed from [pattern]
 */
internal fun tokenisePattern(pattern: String): List<RenderToken> = buildList {
    var state = TokeniserState.NONE
    var previousToken: RenderToken = NoToken
    var previousState = TokeniserState.NONE
    var tokenWidth = StringBuilder()
    var tokenString = StringBuilder()

    /**
     * Add a token to the list and store it as the previous one.
     */
    fun addToken(token: RenderToken) {
        previousToken = token
        add(token)
    }

    /**
     * Set the new state, storing the previous state for reference.
     */
    fun setState(newState: TokeniserState) {
        previousState = state
        state = newState
    }

    /**
     * Evaluate a token width string as an integer, with zero as the
     * default value is no width is specified or the value cannot be
     * parsed as an [Int].
     */
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

    /**
     * Process a recognised token key. It may specify a new token or
     * be appended to the current token string.
     */
    fun processTokenKey(ch: Char, idx: Int) {
        when (state) {
            TokeniserState.IN_PERCENT -> {
                tokens[ch]?.let { creator ->
                    addToken(creator(tokenWidthAsInt(idx + 1)))
                }
                tokenWidth = StringBuilder()
                setState(TokeniserState.NONE)
            }

            else -> {
                if (state == TokeniserState.NONE) {
                    tokenString = StringBuilder()
                    setState(TokeniserState.IN_STRING)
                }
                tokenString.append(ch)
            }
        }
    }

    pattern.forEachIndexed { idx, ch ->
        when (ch) {
            '%' -> when (state) {
                // Start a new token.
                TokeniserState.NONE -> setState(TokeniserState.IN_PERCENT)
                // Output a '%'.
                TokeniserState.IN_PERCENT -> {
                    addToken(StringToken("%"))
                    setState(TokeniserState.NONE)
                }

                // Start a new token, terminating an open StringToken.
                TokeniserState.IN_STRING -> {
                    addToken(StringToken(tokenString.toString()))
                    setState(TokeniserState.IN_PERCENT)
                }

                // Part of a format string.
                TokeniserState.IN_FORMAT -> {
                    tokenString.append(ch)
                }
            }

            in tokens.keys -> processTokenKey(ch, idx)

            // Digit or minus sign.
            in digits -> when (state) {
                // Start of a new string.
                TokeniserState.NONE -> {
                    tokenString = StringBuilder()
                    tokenString.append(ch)
                    setState(TokeniserState.IN_STRING)
                }

                // Append to token width string.
                TokeniserState.IN_PERCENT -> {
                    tokenWidth.append(ch)
                }

                // Part of a string token.
                TokeniserState.IN_STRING -> {
                    tokenString.append(ch)
                }

                // Part of a format string.
                TokeniserState.IN_FORMAT -> {
                    tokenString.append(ch)
                }
            }

            else -> when (state) {
                TokeniserState.NONE -> {
                    // New string token.
                    tokenString = StringBuilder()
                    if (ch == '{' && previousState == TokeniserState.IN_PERCENT) {
                        // Start of a format string.
                        setState(TokeniserState.IN_FORMAT)
                    } else {
                        // Part of a string token.
                        tokenString.append(ch)
                        setState(TokeniserState.IN_STRING)
                    }
                }

                TokeniserState.IN_PERCENT -> {
                    // Not a token key: ignore.
                    warn("tokeniser", "Pattern \"$pattern\" contains invalid token \"%$ch\" at character $idx")
                    setState(TokeniserState.NONE)
                }

                // Part of a string token.
                TokeniserState.IN_STRING -> {
                    tokenString.append(ch)
                }

                TokeniserState.IN_FORMAT -> {
                    when (ch) {
                        '}' -> {
                            // End of format string.
                            if (previousToken != NewlineToken) {
                                previousToken.format = tokenString.toString()
                            }
                            setState(TokeniserState.NONE)
                        }

                        // Part of string token.
                        else -> tokenString.append(ch)
                    }
                }
            }
        }
    }
    // Append any trailing characters as a string token.
    if (state == TokeniserState.IN_STRING) {
        addToken(StringToken(tokenString.toString()))
    }
    // Ignore an incomplete format string and warn about it.
    if (state == TokeniserState.IN_FORMAT) {
        warn("tokeniser", "Pattern \"$pattern\" ends with incomplete format specification")
    }
}
