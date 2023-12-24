/*

   Copyright 2021-2023 Michael Strasser.

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

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent

/**
 * Logger interface for sending log events from `suspend` functions running in
 * [Kotlin coroutines](https://kotlinlang.org/docs/coroutines-overview.html).
 *
 * Every function is a `suspend` function and should only be called in another
 * `suspend` function.
 *
 * @see NoCoLogger for the corresponding non-coroutine interface.
 */
public interface Klogger : BaseLogger {

    /**
     * Emit an event after minimum level checking.
     * @param level [Level] of the event
     * @param throwable possible [Throwable] associated with the event
     * @param event [LogEvent] or other object to emit
     */
    public suspend fun emitEvent(level: Level, throwable: Throwable?, event: Any?)

    public suspend fun log(level: Level, throwable: Throwable, event: Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, throwable, event)
    }

    public suspend fun log(level: Level, event: Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, null, event)
    }

    public suspend fun log(
        level: Level,
        throwable: Throwable,
        template: String,
        vararg values: Any?,
    ) {
        if (!isLevelEnabled(level)) return
        if (values.isEmpty()) {
            emitEvent(level, throwable, template)
        } else {
            emitEvent(level, throwable, e(template, *values))
        }
    }

    public suspend fun log(level: Level, template: String, vararg values: Any?) {
        if (!isLevelEnabled(level)) return
        if (values.isEmpty()) {
            emitEvent(level, null, template)
        } else {
            emitEvent(level, null, e(template, *values))
        }
    }

    public suspend fun log(level: Level, throwable: Throwable, event: suspend Klogger.() -> Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, throwable, event())
    }

    public suspend fun log(level: Level, event: suspend Klogger.() -> Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, null, event())
    }

    public suspend fun trace(event: Any?): Unit = log(TRACE, event)
    public suspend fun trace(template: String, vararg values: Any?): Unit =
        log(TRACE, template, *values)

    public suspend fun trace(throwable: Throwable, event: Any?): Unit =
        log(WARN, throwable, event)

    public suspend fun trace(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(TRACE, throwable, template, *values)

    public suspend fun trace(event: suspend Klogger.() -> Any?): Unit =
        log(TRACE, event)

    public suspend fun trace(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(TRACE, throwable, event)

    public suspend fun debug(event: Any?): Unit = log(DEBUG, event)
    public suspend fun debug(template: String, vararg values: Any?): Unit =
        log(DEBUG, template, *values)

    public suspend fun debug(throwable: Throwable, event: Any?): Unit =
        log(DEBUG, throwable, event)

    public suspend fun debug(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(DEBUG, throwable, template, *values)

    public suspend fun debug(event: suspend Klogger.() -> Any?): Unit =
        log(DEBUG, event)

    public suspend fun debug(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(DEBUG, throwable, event)

    public suspend fun info(event: Any?): Unit = log(INFO, event)
    public suspend fun info(template: String, vararg values: Any?): Unit =
        log(INFO, template, *values)

    public suspend fun info(throwable: Throwable, event: Any?): Unit =
        log(INFO, throwable, event)

    public suspend fun info(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(INFO, throwable, template, *values)

    public suspend fun info(event: suspend Klogger.() -> Any?): Unit =
        log(INFO, event)

    public suspend fun info(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(INFO, throwable, event)

    public suspend fun warn(event: Any?): Unit = log(WARN, event)
    public suspend fun warn(template: String, vararg values: Any?): Unit =
        log(WARN, template, *values)

    public suspend fun warn(throwable: Throwable, event: Any?): Unit =
        log(WARN, throwable, event)

    public suspend fun warn(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(WARN, throwable, template, *values)

    public suspend fun warn(event: suspend Klogger.() -> Any?): Unit =
        log(WARN, event)

    public suspend fun warn(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(WARN, throwable, event)

    public suspend fun error(event: Any?): Unit = log(ERROR, event)
    public suspend fun error(template: String, vararg values: Any?): Unit =
        log(ERROR, template, *values)

    public suspend fun error(throwable: Throwable, event: Any?): Unit =
        log(ERROR, throwable, event)

    public suspend fun error(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(ERROR, throwable, template, *values)

    public suspend fun error(event: suspend Klogger.() -> Any?): Unit =
        log(ERROR, event)

    public suspend fun error(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(ERROR, throwable, event)

    public suspend fun fatal(event: Any?): Unit = log(FATAL, event)

    public suspend fun fatal(template: String, vararg values: Any?): Unit =
        log(FATAL, template, *values)

    public suspend fun fatal(throwable: Throwable, event: Any?): Unit =
        log(FATAL, throwable, event)

    public suspend fun fatal(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(FATAL, throwable, template, *values)

    public suspend fun fatal(event: suspend Klogger.() -> Any?): Unit =
        log(FATAL, event)

    public suspend fun fatal(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(FATAL, throwable, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public suspend fun e(template: String, vararg values: Any?): LogEvent
}
