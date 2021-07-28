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

import io.klogging.config.LoggingConfiguration
import io.klogging.events.LogEvent
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Type used for dispatching a [LogEvent] somewhere. */
public typealias DispatchEvent = (LogEvent) -> Unit

/** Simple dispatcher that sends a single, formatted line to the console. */
public val simpleDispatcher: DispatchEvent =
    { e -> println("${e.timestamp} [${e.level}] ${e.items} - ${e.logger} - ${e.message}") }

/** Object that handles dispatching of [LogEvent]s to zero or more targets. */
public object Dispatcher {

    /**
     * Dispatch a [LogEvent] to selected targets.
     *
     * Each is dispatched in a separate coroutine.
     */
    public suspend fun dispatchEvent(logEvent: LogEvent): Unit = coroutineScope {
        LoggingConfiguration
            .dispatchersFor(logEvent.logger, logEvent.level)
            .forEach { launch { it.dispatcher(logEvent) } }
    }
}
