/*

   Copyright 2021-2022 Michael Strasser.

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

package io.klogging.internal

import io.klogging.Level
import io.klogging.Level.TRACE
import io.klogging.events.LogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/** Object that handles dispatching of [LogEvent]s to zero or more sinks. */
internal object Dispatcher : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = kloggingParentContext

    private val sinkCache = mutableMapOf<String, List<Sink>>()

    /**
     * Dispatch a [LogEvent] to selected targets. Base context items are included here.
     *
     * Each is dispatched in a separate coroutine.
     */
    internal fun send(logEvent: LogEvent) {
        // If we are tracing Klogging, add event ID to the items map.
        val traceContext = if (KloggingEngine.kloggingMinLogLevel() == TRACE) {
            mapOf("eventId" to logEvent.id)
        } else {
            mapOf()
        }

        val event = logEvent.addContext(traceContext + KloggingEngine.baseContextItems)

        cachedSinksFor(logEvent.logger, logEvent.level)
            .forEach { sink ->
                launch {
                    trace("Dispatcher", "Dispatching event ${event.id} to ${sink.name}")
                    sink.send(event)
                }
            }
    }

    /**
     * Simple caching wrapper for [sinksFor] function.
     */
    internal fun cachedSinksFor(loggerName: String, level: Level): List<Sink> =
        sinkCache[loggerName] ?: sinksFor(loggerName, level)

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
            .flatMap { it.ranges }
            .filter { level in it }
            .flatMap { it.sinkNames }
            .distinct()
        return KloggingEngine.sinks()
            .filterKeys { it in sinkNames }
            .map { it.value }
    }
}
