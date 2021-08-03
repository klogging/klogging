/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.events

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.TimeZone

/** Right now, expressed as a [Timestamp]. */
public actual fun now(): Timestamp {
    val instant = Instant.now()
    return Timestamp(instant.epochSecond, instant.nano.toLong())
}

/** Express a [Timestamp] as an ISO8601-formatted string. */
internal actual fun iso(timestamp: Timestamp): String =
    Instant.ofEpochSecond(timestamp.epochSeconds, timestamp.nanos)
        .toString()

private val localOffset: ZoneOffset = ZoneOffset.ofTotalSeconds(TimeZone.getDefault().rawOffset / 1000)

/** Express a [Timestamp] as an ISO8601 local timezone string without the `T`. */
internal actual fun local(timestamp: Timestamp): String = LocalDateTime
    .ofEpochSecond(timestamp.epochSeconds, timestamp.nanos.toInt(), localOffset)
    .toString()
    .replace('T', ' ')
