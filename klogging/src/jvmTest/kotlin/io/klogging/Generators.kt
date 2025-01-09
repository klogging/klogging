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

package io.klogging

import io.klogging.events.LogEvent
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.exhaustive.enum

fun Codepoint.Companion.identifier(): Arb<Codepoint> =
    Arb.of(((('a'..'z') + ('A'..'Z')).toList() + listOf('.', '_')).map { Codepoint(it.code) })
        .withEdgecases(Codepoint('.'.code))

class TestException(message: String) : Exception(message)

val genLoggerName = Arb.string(1, 60, Codepoint.identifier())
val genLevel = Exhaustive.enum<Level>()
val genMessage = Arb.string(1, 120)
val genString = Arb.string(1, 20, Codepoint.alphanumeric())
val genException = genMessage.map { message -> Exception(message).fillInStackTrace() }
val genLogEvent = Arb.bind(genLoggerName, genLevel.toArb(), genMessage) { name, level, message ->
    LogEvent(logger = name, level = level, message = message)
}
val genItem = Arb.bind(genString, genString) { key, value -> key to value }
