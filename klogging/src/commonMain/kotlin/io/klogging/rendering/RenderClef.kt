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

import io.klogging.events.LogEvent

/**
 * Render a [LogEvent] into [CLEF](https://clef-json.org) compact JSON format.
 *
 * - Any items with destructuring indicators (names starting with `@`) are destructured into maps
 *   before deserialisation.
 * - If `context` is not null, include it with key `context`.
 * - If `template` is not null, include it with key `@mt`, else include `message` with key `@m`.
 * - If `stackTrace` is not null, include it with key `@x`.
 */
public val RENDER_CLEF: RenderString = RenderString { event ->
    val eventMap: MutableMap<String, Any?> = (
            mapOf(
                "@t" to event.timestamp.toString(),
                "@l" to event.level.name,
                "host" to event.host,
                "logger" to event.logger,
            ) + event.items.destructured
            ).toMutableMap()
    if (event.context != null) eventMap["context"] = event.context
    if (event.template != null)
        eventMap["@mt"] = event.template
    else
        eventMap["@m"] = event.message
    if (event.stackTrace != null) eventMap["@x"] = event.stackTrace

    serializeMap(eventMap, omitNullValues = false)
}
