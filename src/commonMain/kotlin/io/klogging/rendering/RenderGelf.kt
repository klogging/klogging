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

package io.klogging.rendering

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import kotlinx.datetime.Instant

private const val GELF_TEMPLATE =
    """{"version":"1.1","host":"{{HOST}}","short_message":"{{SHORT}}",{{EX}}"timestamp":{{TS}},"level":{{LEVEL}},{{ITEMS}}}"""
private const val STACK_TEMPLATE = """"full_message":"{{ST}}","""

/**
 * Renders a [LogEvent] into [GELF](https://docs.graylog.org/en/latest/pages/gelf.html#gelf-payload-specification)
 * JSON format.
 *
 * It uses very crude string templates because JSON serialisation is
 * unable to convert an [Instant] into a number in `ssssssssssss.nnnnnnnnn`
 * format as required for GELF.
 */
public val RENDER_GELF: RenderString = { e ->
    val exception = e.stackTrace?.let { formatStackTrace(it) } ?: ""
    val itemsJson = (e.items + mapOf("logger" to e.logger))
        .map { (k, v) -> """"_$k":"$v"""" }
        .joinToString(",")

    GELF_TEMPLATE
        .replace("{{HOST}}", e.host)
        .replace("{{SHORT}}", e.message)
        .replace("{{EX}}", exception)
        .replace("{{TS}}", e.timestamp.graylogFormat())
        .replace("{{LEVEL}}", graylogLevel(e.level).toString())
        .replace("{{ITEMS}}", itemsJson)
}

private fun formatStackTrace(stackTrace: String) = STACK_TEMPLATE
    .replace("{{ST}}", stackTrace)

public fun Instant.graylogFormat(): String {
    val ns = "000000000$nanosecondsOfSecond"
    return "$epochSeconds.${ns.substring(ns.length - 9)}"
}

/**
 * Map [Level]s to syslog levels used by Graylog:
 *
 * 0=Emergency,1=Alert,2=Critical,3=Error,4=Warning,5=Notice,6=Informational,7=Debug
 *
 * See [syslog(7)](https://www.man7.org/linux/man-pages/man3/syslog.3.html)
 */
public fun graylogLevel(level: Level): Int = when (level) {
    NONE -> 7
    TRACE -> 7
    DEBUG -> 7
    INFO -> 6
    WARN -> 4
    ERROR -> 3
    FATAL -> 2 // TODO: Why "critical" and not "alert"?  Perhaps explanatory text in the kdoc?
}
