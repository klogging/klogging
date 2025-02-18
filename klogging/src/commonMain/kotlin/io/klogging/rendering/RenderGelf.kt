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

import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.klogging.events.decimalSeconds
import io.klogging.syslog
import kotlinx.datetime.Instant

/**
 * Marker used to enable decimal timestamps of the form ssssssssss.nnnnnnnnn
 * (i.e. not strings).
 */
private const val TIME_MARKER = "XXX--TIME-MARKER--XXX"

/**
 * Renders a [LogEvent] into [GELF](https://docs.graylog.org/en/latest/pages/gelf.html#gelf-payload-specification)
 * JSON format.
 */
public val RENDER_GELF: RenderString = RenderString { event ->
    val eventMap: EventItems = (
            mapOf(
                "version" to "1.1",
                "host" to event.host,
                "short_message" to event.evalTemplate(),
                "full_message" to event.stackTrace,
                "timestamp" to TIME_MARKER,
                "level" to event.level.syslog,
                "_logger" to event.logger,
            ) + event.items.destructured.mapKeys { (k, _) -> "_$k" }
            ).filterValues { it != null }

    serializeMap(eventMap)
        .replace(""""$TIME_MARKER"""", event.timestamp.decimalSeconds)
}

public fun Instant.graylogFormat(): String {
    val ns = "000000000$nanosecondsOfSecond"
    return "$epochSeconds.${ns.substring(ns.length - 9)}"
}
