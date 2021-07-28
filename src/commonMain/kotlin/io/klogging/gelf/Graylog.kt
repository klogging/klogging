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

package io.klogging.gelf

import io.klogging.events.Level
import io.klogging.events.Timestamp

/**
 * Map [Level]s to syslog levels used by Graylog:
 *
 * 0=Emergency,1=Alert,2=Critical,3=Error,4=Warning,5=Notice,6=Informational,7=Debug
 */
public fun graylogLevel(level: Level): Int = when (level) {
    Level.NONE -> 7
    Level.TRACE -> 7
    Level.DEBUG -> 7
    Level.INFO -> 6
    Level.WARN -> 4
    Level.ERROR -> 3
    Level.FATAL -> 2
}

public data class Endpoint(
    val host: String = "localhost",
    val port: Int = 12201,
)

public fun Timestamp.graylogFormat(): String {
    val ns = "000000000$nanos"
    return "$epochSeconds.${ns.substring(ns.length - 9)}"
}
