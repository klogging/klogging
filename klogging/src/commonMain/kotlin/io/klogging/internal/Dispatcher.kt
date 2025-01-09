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

package io.klogging.internal

import io.klogging.AtomicMutableMap
import io.klogging.Level
import io.klogging.Level.TRACE
import io.klogging.events.LogEvent

/** Object that handles dispatching of [LogEvent]s to zero or more sinks. */
internal object Dispatcher {

    @Suppress("TYPE_ALIAS")
    private val sinkCache: AtomicMutableMap<Pair<String, Level>, List<Sink>> = AtomicMutableMap()

    /**
     * Dispatch a [LogEvent] to selected targets. Base context items are included here.
     *
     * Each is dispatched in a separate coroutine.
     *
     * @param logEvent event to dispatch
     */
    internal suspend fun send(logEvent: LogEvent) {
        // If we are tracing Klogging, add event ID to the items map.
        val event = logEvent.addContext(traceContext(logEvent) + KloggingEngine.baseContextItems)

        cachedSinksFor(logEvent.logger, logEvent.level)
            .forEach { sink ->
                trace("Dispatcher", "Dispatching event ${event.id} to sink ${sink.name}")
                sink.send(event)
            }
    }

    /**
     * Dispatch a [LogEvent] directly to each sink.
     * @param logEvent event to dispatch directly
     */
    internal fun sendDirect(logEvent: LogEvent) {
        // If we are tracing Klogging, add event ID to the items map.
        val event = logEvent.addContext(traceContext(logEvent) + KloggingEngine.baseContextItems)

        cachedSinksFor(logEvent.logger, logEvent.level)
            .forEach { sink ->
                trace("Dispatcher", "Dispatching event ${event.id} directly to sink ${sink.name}")
                sink.sendDirect(event)
            }
    }

    private fun traceContext(logEvent: LogEvent) =
        if (KloggingEngine.kloggingMinLogLevel() == TRACE) {
            mapOf("eventId" to logEvent.id)
        } else {
            mapOf()
        }

    /**
     * Simple caching wrapper for [sinksFor] function.
     * @param loggerName name of the logger
     * @param level: level of the event
     * @return sinks to dispatch from the specified logger and level
     */
    internal fun cachedSinksFor(loggerName: String, level: Level): List<Sink> =
        sinkCache.getOrPut(Pair(loggerName, level)) { sinksFor(loggerName, level) }

    /**
     * Clear the internal cache of sinks, to be called whenever the global Klogging
     * sinks are set [KloggingEngine.setSinks].
     */
    internal fun clearCache() {
        sinkCache.clear()
    }

    /**
     * Calculate the sinks for the specified logger and level.
     *
     * Logging configurations are evaluated in the order they are specified.
     * Matching stops on successful match if the `stopOnMatch` property is
     * `true` (it is false by default).
     *
     * @param loggerName name of the logger
     * @param level level at which to emit logs
     *
     * @return the list of [Sink]s for this logger at this level, which may be empty
     */
    internal fun sinksFor(loggerName: String, level: Level): List<Sink> {
        var keepMatching = true
        val sinkNames = KloggingEngine.configs()
            .filter { config ->
                val matches = config.nameMatcher(loggerName)
                (keepMatching && matches).also {
                    keepMatching = keepMatching && !(matches && config.stopOnMatch)
                }
            }
            .flatMap { config -> config.ranges }
            .filter { range -> level in range }
            .flatMap { range -> range.sinkNames }
            .distinct()
        return KloggingEngine.sinks()
            .filterKeys { key -> key in sinkNames }
            .map { entry -> entry.value }
    }
}
