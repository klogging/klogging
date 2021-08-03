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

/** Right now, expressed as a [Timestamp]. */
public actual fun now(): Timestamp {
    TODO("Not yet implemented")
}

/** Express a [Timestamp] as an ISO8601-formatted string. */
internal actual fun iso(timestamp: Timestamp): String {
    TODO("Not yet implemented")
}

/** Express a [Timestamp] as an ISO8601 local timezone string without the `T`. */
internal actual fun local(timestamp: Timestamp): String {
    TODO("Not yet implemented")
}
