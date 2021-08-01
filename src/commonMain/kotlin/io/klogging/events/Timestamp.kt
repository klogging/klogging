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

/**
 * Timestamp model, comprising seconds since the Unix Epoch and
 * nanoseconds within the second.
 */
public data class Timestamp(val epochSeconds: Long, val nanos: Long) {
    override fun toString(): String = iso(this)
}

/** Right now, expressed as a [Timestamp]. */
public expect fun now(): Timestamp

/** Convert a [Timestamp] into an ISO8601-formatted string. */
public expect fun iso(timestamp: Timestamp): String
