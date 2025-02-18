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
 * Render a [LogEvent] into JSON in
 * [Elastic Common Schema](https://www.elastic.co/guide/en/ecs/current/index.html) for sending
 * directly to an [ELK](https://www.elastic.co/what-is/elk-stack) stack.
 *
 * It populates ECS fields as follows:
 *
 * - `timestamp` -> `@timestamp`
 * - `host` -> `host.name`
 * - `logger` -> `log.logger`
 * - `level` -> `log.level`
 * - `message` (evaluated template) -> `message`
 * - If `stackTrace` is present:
 *   - `stackTrace` -> `error.stack_trace`
 *   - `message` (evaluated template) -> `error.message`
 * - `context` (if present) -> `context` in `labels` object
 * - `items` (if present) -> `items` (object)
 */
public val RENDER_ECS: RenderString = RenderString { event ->
    val eventMap: MutableMap<String, Any?> = mutableMapOf(
        "@timestamp" to event.timestamp,
        "host.name" to event.host,
        "log.logger" to event.logger,
        "log.level" to event.level.name,
        "message" to event.evalTemplate(),
        "error.stack_trace" to event.stackTrace,
        "error.message" to event.stackTrace?.let { event.evalTemplate() },
    )
    if (event.context != null) {
        eventMap += "labels" to mapOf("context" to event.context)
    }
    if (event.items.isNotEmpty()) {
        eventMap += "items" to event.items.destructured
    }

    serializeMap(eventMap.filterValues { it != null })
}
