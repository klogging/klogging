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

import io.klogging.events.LogEvent
import io.klogging.json.serializeMap

/**
 * Renders a [LogEvent] into [CLEF](https://docs.datalust.co/docs/posting-raw-events#compact-json-format)
 * compact JSON format.
 *
 * - If `context` is not null, include it with key `context`.
 * - If `template` is not null, include it with key `@mt`, else include `message` with key `@m`.
 * - If `stackTrace` is not null, include it with key `@x`.
 */
public val RENDER_CLEF: RenderString = { e ->
    val eventMap: MutableMap<String, Any?> = (
        mapOf(
            "@t" to e.timestamp.isoString,
            "@l" to e.level.name,
            "host" to e.host,
            "logger" to e.logger,
        ) + e.items
        ).toMutableMap()
    if (e.context != null) eventMap["context"] = e.context
    if (e.template != null) eventMap["@mt"] = e.template else eventMap["@m"] = e.message
    if (e.stackTrace != null) eventMap["@x"] = e.stackTrace

    serializeMap(eventMap)
}
