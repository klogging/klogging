/*

   Copyright 2022 Michael Strasser.

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
 * Interface for logging from outside coroutines.
 *
 * `NoCoLogger` is a working name for now.
 */
public interface NoCoLogger : BaseLogger {

    /**
     * Emit an event after minimum level checking.
     *
     * This is different to [Klogger#emitEvent] in that it optionally accepts a map of
     * items from the context. This mechanism is used by the
     * [SLF4J binding](https://github.com/klogging/slf4j-klogging).
     */
    public fun emitEvent(
        level: Level,
        throwable: Throwable?,
        event: Any?,
        contextItems: Map<String, Any?> = mapOf(),
    )

    public fun log(level: Level, throwable: Throwable, event: Any?): Unit =
        emitEvent(level, throwable, event)

    public fun log(level: Level, event: Any?): Unit = emitEvent(level, null, event)

    public fun log(
        level: Level,
        throwable: Throwable,
        template: String,
        vararg values: Any?
    ): Unit =
        if (values.isEmpty()) emitEvent(level, throwable, template)
        else emitEvent(level, throwable, e(template, *values))

    public fun log(level: Level, template: String, vararg values: Any?): Unit =
        if (values.isEmpty()) emitEvent(level, null, template)
        else emitEvent(level, null, e(template, *values))

    public fun trace(event: Any?): Unit = log(TRACE, event)
    public fun debug(event: Any?): Unit = log(DEBUG, event)
    public fun info(event: Any?): Unit = log(INFO, event)
    public fun warn(event: Any?): Unit = log(WARN, event)
    public fun error(event: Any?): Unit = log(ERROR, event)
    public fun fatal(event: Any?): Unit = log(FATAL, event)

    public fun trace(template: String, vararg values: Any?): Unit = log(TRACE, template, *values)
    public fun debug(template: String, vararg values: Any?): Unit = log(DEBUG, template, *values)
    public fun info(template: String, vararg values: Any?): Unit = log(INFO, template, *values)
    public fun warn(template: String, vararg values: Any?): Unit = log(WARN, template, *values)
    public fun error(template: String, vararg values: Any?): Unit = log(ERROR, template, *values)
    public fun fatal(template: String, vararg values: Any?): Unit = log(FATAL, template, *values)

    public fun trace(throwable: Throwable, event: Any?): Unit = log(TRACE, throwable, event)
    public fun debug(throwable: Throwable, event: Any?): Unit = log(DEBUG, throwable, event)
    public fun info(throwable: Throwable, event: Any?): Unit = log(INFO, throwable, event)
    public fun warn(throwable: Throwable, event: Any?): Unit = log(WARN, throwable, event)
    public fun error(throwable: Throwable, event: Any?): Unit = log(ERROR, throwable, event)
    public fun fatal(throwable: Throwable, event: Any?): Unit = log(FATAL, throwable, event)

    public fun trace(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(TRACE, throwable, template, *values)

    public fun debug(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(DEBUG, throwable, template, *values)

    public fun info(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(INFO, throwable, template, *values)

    public fun warn(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(WARN, throwable, template, *values)

    public fun error(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(ERROR, throwable, template, *values)

    public fun fatal(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(FATAL, throwable, template, *values)

    public fun log(level: Level, throwable: Throwable, event: NoCoLogger.() -> Any?) {
        if (isLevelEnabled(level)) emitEvent(level, throwable, event())
    }

    public fun log(level: Level, event: NoCoLogger.() -> Any?) {
        if (isLevelEnabled(level)) emitEvent(level, null, event())
    }

    public fun trace(event: NoCoLogger.() -> Any?): Unit = log(TRACE, event)
    public fun debug(event: NoCoLogger.() -> Any?): Unit = log(DEBUG, event)
    public fun info(event: NoCoLogger.() -> Any?): Unit = log(INFO, event)
    public fun warn(event: NoCoLogger.() -> Any?): Unit = log(WARN, event)
    public fun error(event: NoCoLogger.() -> Any?): Unit = log(ERROR, event)
    public fun fatal(event: NoCoLogger.() -> Any?): Unit = log(FATAL, event)

    public fun trace(throwable: Throwable, event: NoCoLogger.() -> Any?): Unit =
        log(TRACE, throwable, event)

    public fun debug(throwable: Throwable, event: NoCoLogger.() -> Any?): Unit =
        log(DEBUG, throwable, event)

    public fun info(throwable: Throwable, event: NoCoLogger.() -> Any?): Unit =
        log(INFO, throwable, event)

    public fun warn(throwable: Throwable, event: NoCoLogger.() -> Any?): Unit =
        log(WARN, throwable, event)

    public fun error(throwable: Throwable, event: NoCoLogger.() -> Any?): Unit =
        log(ERROR, throwable, event)

    public fun fatal(throwable: Throwable, event: NoCoLogger.() -> Any?): Unit =
        log(FATAL, throwable, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public fun e(template: String, vararg values: Any?): LogEvent
}
