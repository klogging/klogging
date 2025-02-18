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

import io.klogging.config.evalEnv
import io.klogging.events.EventItems
import io.klogging.events.decimalSeconds

private const val TIME_MARKER = "XXX--TIME-MARKER--XXX"

/**
 * Renders an event into JSON format for Splunk HTTP Event Collector.
 *
 * @param index The index for the event. Defaults to null if not provided.
 * @param sourceType The source type for the event. Defaults to null if not provided.
 * @param source The source for the event. Defaults to null if not provided.
 * @return The rendered event as a string in JSON format for Splunk HEC.
 */
public fun renderHec(
    index: String? = null,
    sourceType: String? = null,
    source: String? = null,
): RenderString = RenderString { event ->
    val eventMap: EventItems = (
            mapOf(
                "logger" to event.logger,
                "level" to event.level.name,
                "context" to event.context,
                "stackTrace" to event.stackTrace,
                "message" to event.evalTemplate(),
            ) + event.items.destructured
            ).filterValues { it != null }
    val splunkMap: MutableMap<String, Any?> = mutableMapOf(
        "time" to TIME_MARKER,
        "index" to index?.let { evalEnv(it) },
        "sourcetype" to sourceType?.let { evalEnv(it) },
        "source" to source?.let { evalEnv(it) },
        "host" to event.host,
        "event" to eventMap,
    )
    serializeMap(splunkMap)
        .replace(""""$TIME_MARKER"""", event.timestamp.decimalSeconds)
}
