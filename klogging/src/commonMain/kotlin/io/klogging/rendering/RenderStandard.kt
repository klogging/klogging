/*

   Copyright 2024-2025 Baris Ceviz and Michael Strasser.

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
 * Render a [LogEvent] into structured JSON log format.
 *
 * - If `context` is not null, include it with key `context`.
 * - If `stackTrace` is not null, include it with key `stackTrace`.
 */
public val RENDER_STANDARD: RenderString = RenderString { event ->
    val eventMap: MutableMap<String, Any?> = (
            mapOf(
                "timestamp" to event.timestamp.toString(),
                "level" to event.level.name,
                "host" to event.host,
                "logger" to event.logger,
            ) + event.items.destructured
            ).toMutableMap()
    if (event.context != null) eventMap["context"] = event.context
    eventMap["message"] = event.evalTemplate()
    if (event.stackTrace != null) eventMap["stackTrace"] = event.stackTrace

    serializeMap(eventMap)
}
