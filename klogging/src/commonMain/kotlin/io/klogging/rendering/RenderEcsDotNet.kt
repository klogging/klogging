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
 * Render a [LogEvent] into JSON to mimic that described in
 * [Elastic Common Schema for .NET](https://github.com/elastic/ecs-dotnet/blob/main/examples/aspnetcore-with-serilog/).
 *
 * It populates ECS fields as follows:
 *
 * - `timestamp` -> `@timestamp`
 * - `host` -> `host.name`
 * - `logger` -> a `log` object with `logger` field
 * - `level` -> `log.level`
 * - `message` (evaluated template) -> `message`
 * - `template` (if present) -> `message_template` field in `metadata` object
 * - If `stackTrace` is present:
 *   - `stackTrace` -> `error.stack_trace`
 *   - `message` (evaluated template) -> `error.message`
 * - `context` (if present) -> `context` in `labels` object
 * - `items` (if present) -> `metadata` (object)
 */
public val RENDER_ECS_DOTNET: RenderString = RenderString { event ->
    val eventMap: MutableMap<String, Any?> = mutableMapOf(
        "@timestamp" to event.timestamp,
        "host.name" to event.host,
        "log" to mapOf("logger" to event.logger),
        "log.level" to event.level.name,
        "message" to event.evalTemplate(),
        "error.stack_trace" to event.stackTrace,
        "error.message" to event.stackTrace?.let { event.evalTemplate() },
    )
    if (event.context != null) {
        eventMap += "labels" to mapOf("context" to event.context)
    }
    val metadata = mutableMapOf<String, Any?>().apply { putAll(event.items.destructured) }
    if (event.template != null) {
        metadata += "message_template" to event.template
    }
    if (metadata.isNotEmpty()) {
        eventMap += "metadata" to metadata
    }

    serializeMap(eventMap.filterValues { it != null })
}
