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

package io.klogging.events

import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant

/**
 * Timestamp of "now" defined by [clock].  The default is the current system clock (time).
 * @param clock [Clock] to use
 * @return a timestamp using the specified clock
 */
public fun timestampNow(clock: Clock = System): Instant = clock.now()

private const val NANOS_LENGTH: Int = 9

/** Render a Kotlin [Instant] as `seconds.nanos`. */
@Suppress("CUSTOM_GETTERS_SETTERS")
internal val Instant.decimalSeconds: String
    get() {
        val seconds = epochSeconds.toString()
        val nanos = "00000000$nanosecondsOfSecond".let { it.substring(it.length - NANOS_LENGTH) }
        return "$seconds.$nanos"
    }
