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

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class TokenisePatternTest : DescribeSpec({
    describe("`tokenisePattern` function") {
        it("returns empty list from empty string") {
            tokenisePattern("") shouldBe emptyList()
        }
        describe("returns a single token if that is found") {
            withData(
                mapOf(
                    "StringToken" to ("{LOCAL}" to StringToken("{LOCAL}")),
                    "StringToken" to (" : " to StringToken(" : ")),
                    "TimestampToken" to ("%t" to TimestampToken()),
                    "HostToken" to ("%h" to HostToken()),
                    "LoggerToken" to ("%l" to LoggerToken()),
                    "ContextToken" to ("%c" to ContextToken()),
                    "LevelToken" to ("%v" to LevelToken()),
                    "MessageToken" to ("%m" to MessageToken()),
                    "StacktraceToken" to ("%s" to StacktraceToken()),
                    "ItemsToken" to ("%i" to ItemsToken),
                    "NewlineToken" to ("%n" to NewlineToken),
                )
            ) { (input, expected) ->
                tokenisePattern(input) shouldBe listOf(expected)
            }
        }
        it("returns tokens found in the string") {
            tokenisePattern("%t : %h : %l : %c : %v : %m : %i : %s : %n")
                .shouldContainInOrder(
                    TimestampToken(),
                    StringToken(" : "),
                    HostToken(),
                    StringToken(" : "),
                    LoggerToken(),
                    StringToken(" : "),
                    ContextToken(),
                    StringToken(" : "),
                    LevelToken(),
                    StringToken(" : "),
                    MessageToken(),
                    StringToken(" : "),
                    ItemsToken,
                    StringToken(" : "),
                    StacktraceToken(),
                    StringToken(" : "),
                    NewlineToken,
                )
        }
        it("returns a string token at the end if found") {
            tokenisePattern("%m!").shouldContainInOrder(
                MessageToken(),
                StringToken("!"),
            )
        }
        it("ignores invalid tokens") {
            tokenisePattern("%X").shouldBeEmpty()
        }
        it("returns ‘%’ for ‘%%’") {
            tokenisePattern("%t %%%n").shouldContainInOrder(
                TimestampToken(),
                StringToken(" "),
                StringToken("%"),
                NewlineToken,
            )
        }
        it("formats a token if the formatting string follows immediately") {
            with(tokenisePattern("%4v{COLOUR}").first()) {
                shouldBe(LevelToken(4))
                format shouldBe "COLOUR"
            }
        }
        it("ignores formatting that is not immediately after a token") {
            tokenisePattern("%4h {COLOUR}").shouldContainInOrder(
                HostToken(4),
                StringToken(" {COLOUR}"),
            )
        }
        it("ignores formatting that is incomplete") {
            with(tokenisePattern("%v{COLOUR").first()) {
                shouldBe(LevelToken(0))
                format.shouldBeNull()
            }
        }
        it("accepts positive widths for property tokens") {
            tokenisePattern("%11t %1288m").shouldContainInOrder(
                TimestampToken(11),
                StringToken(" "),
                MessageToken(1288),
            )
        }
        it("accepts negative widths for property tokens") {
            tokenisePattern("%-3t %-100m").shouldContainInOrder(
                TimestampToken(-3),
                StringToken(" "),
                MessageToken(-100),
            )
        }
        it("ignores invalid widths of tokens") {
            tokenisePattern("%0-6m").shouldContainExactly(
                MessageToken(0)
            )
        }
    }
})