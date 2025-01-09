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

package io.klogging.impl

import io.klogging.Klogger
import io.klogging.Level
import io.klogging.context.LogContext
import io.klogging.context.otherContextItems
import io.klogging.events.EventItems
import io.klogging.events.LogEvent
import io.klogging.events.contextName
import io.klogging.events.timestampNow
import io.klogging.internal.Emitter
import io.klogging.internal.KloggingEngine
import io.klogging.templating.templateItems
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Standard implementation of [Klogger].
 * @property name logger name
 * @property loggerContextItems context items belonging to this logger
 */
public class KloggerImpl(
    override val name: String,
    override val loggerContextItems: EventItems = mapOf(),
) : Klogger {

    /**
     * Emit an event to be dispatched and sent.
     * @param level logging level for the event
     * @param throwable any [Throwable] associated with the event
     * @param event something to emit: a [LogEvent] or other object
     * @param items explicit items to include in this log event
     */
    public override suspend fun emitEvent(
        level: Level,
        throwable: Throwable?,
        event: Any?,
        items: EventItems
    ) {
        val eventToLog =
            eventFrom(contextName(), level, throwable, event, loggerContextItems + items + contextItems())
        if (eventToLog.level < KloggingEngine.minDirectLogLevel()) {
            Emitter.emit(eventToLog)
        } else {
            Emitter.emitDirect(eventToLog)
        }
    }

    private suspend inline fun contextItems(): EventItems {
        val kloggingContextItems = coroutineContext[LogContext]?.getAll()?.toMutableMap()
            ?: mutableMapOf()
        val coroutineItems = KloggingEngine.otherContextExtractors.entries
            .fold(kloggingContextItems) { contextItems, entry ->
                contextItems.putAll(currentCoroutineContext().otherContextItems(entry.key, entry.value))
                contextItems
            }
        return KloggingEngine.otherItemExtractors
            .fold(coroutineItems) { items, extractor ->
                items.putAll(extractor())
                items
            }
    }

    /**
     * Construct a [LogEvent] from a template and values.
     * @param template [Message template](https://messagetemplates.org) to interpret
     * @param values values corresponding to holes in the template
     * @return a [LogEvent] with context items mapped to the template
     */
    @Suppress("IDENTIFIER_LENGTH")
    public override suspend fun e(template: String, vararg values: Any?): LogEvent {
        val items = templateItems(template, *values)
        return LogEvent(
            timestamp = timestampNow(),
            logger = this.name,
            context = contextName(),
            level = minLevel(),
            template = template,
            message = template,
            stackTrace = null,
            items = items + contextItems(),
        )
    }
}
