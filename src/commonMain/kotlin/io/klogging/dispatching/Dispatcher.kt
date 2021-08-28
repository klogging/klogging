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

package io.klogging.dispatching

import io.klogging.Level
import io.klogging.config.SinkConfiguration
import io.klogging.events.LogEvent
import io.klogging.internal.KloggingState
import io.klogging.internal.debug
import io.klogging.internal.Sink
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Object that handles dispatching of [LogEvent]s to zero or more targets. */
public object Dispatcher {

    /**
     * Dispatch a [LogEvent] to selected targets.
     *
     * Each is dispatched in a separate coroutine.
     */
    public suspend fun dispatchEvent(logEvent: LogEvent): Unit = coroutineScope {
        sinksFor(logEvent.logger, logEvent.level)
            .forEach { sinkConfig ->
                launch {
                    debug("Dispatching event ${logEvent.id}")
                    sinkConfig.dispatcher(sinkConfig.renderer(logEvent))
                }
            }
    }

    public fun sinksFor(loggerName: String, level: Level): List<Sink> {
        val sinkNames = KloggingState.configs()
            .filter { it.nameMatch.matches(loggerName) }
            .flatMap { it.ranges }
            .filter { level in it }
            .flatMap { it.sinkNames }
            .distinct()
        return KloggingState.sinks()
            .filterKeys { it in sinkNames }
            .map { it.value }
    }
}

/** Functional type used for dispatching a string somewhere. */
public typealias DispatchString = (String) -> Unit

