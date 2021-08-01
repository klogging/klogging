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

import io.klogging.config.KloggingConfiguration
import io.klogging.config.SinkConfiguration
import io.klogging.events.Level
import io.klogging.events.LogEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Type used for dispatching a string somewhere. */
public typealias DispatchString = (String) -> Unit

/** Type used for dispatching a [LogEvent] somewhere. */
public typealias DispatchEvent = (LogEvent) -> Unit

/**
 * Simple dispatcher that sends a single, formatted line to the console, with any stack trace
 * on the following lines.
 */
public val simpleDispatcher: DispatchEvent = { e ->
    println("${e.timestamp} ${e.level} [${e.context}] ${e.logger} ${e.items} : ${e.message}")
    if (e.stackTrace != null) println(e.stackTrace)
}

/** Object that handles dispatching of [LogEvent]s to zero or more targets. */
public object Dispatcher {

    /**
     * Dispatch a [LogEvent] to selected targets.
     *
     * Each is dispatched in a separate coroutine.
     */
    public suspend fun dispatchEvent(logEvent: LogEvent): Unit = coroutineScope {
        sinksFor(logEvent.logger, logEvent.level)
            .forEach { launch { it.dispatcher(it.renderer(logEvent)) } }
    }

    public fun sinksFor(loggerName: String, level: Level): List<SinkConfiguration> {
        val sinkNames = KloggingConfiguration.configs
            .filter { it.nameMatch.matches(loggerName) }
            .flatMap { it.ranges }
            .filter { level >= it.minLevel && level <= it.maxLevel }
            .flatMap { it.sinkNames }
            .distinct()
        return KloggingConfiguration.sinks
            .filterKeys { sinkNames.contains(it) }
            .map { it.value }
    }
}
