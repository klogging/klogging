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

package io.klogging.render

import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.events.Timestamp

private const val GELF_TEMPLATE =
    """{"version":"1.1","host":"{{HOST}}","short_message":"{{SHORT}}",{{EX}}"timestamp":{{TS}},"level":{{LEVEL}},{{ITEMS}}}"""
private const val STACK_TEMPLATE = """"full_message":"{{ST}}","""

/**
 * Renders a [LogEvent] into [GELF](https://docs.graylog.org/en/latest/pages/gelf.html#gelf-payload-specification)
 * JSON format.
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

public fun Timestamp.graylogFormat(): String {
    val ns = "000000000$nanos"
    return "$epochSeconds.${ns.substring(ns.length - 9)}"
}
