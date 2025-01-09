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

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.EventItems
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
     * Emit a log event and associated throwable object at the specified severity level.
     *
     * @param level [Level] of the event
     * @param throwable possible throwable object associated with the event
     * @param event a representation of the event, if any
     * @param items explicit items to include in this log event
     */
    public suspend fun emitEvent(
        level: Level,
        throwable: Throwable?,
        event: Any?,
        items: EventItems = mapOf(),
    )

    /**
     * Emit a log event, only if it passes the level check.
     *
     * @param level severity level of the log event
     * @param event a representation of the event, if any
     */
    public suspend fun log(level: Level, event: Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, null, event)
    }

    /**
     * Emit a log event and associated throwable object, only if it passes the level check.
     *
     * @param level severity level of the log event
     * @param throwable a throwable object associated with this event
     * @param event a representation of the event, if any
     */
    public suspend fun log(level: Level, throwable: Throwable, event: Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, throwable, event)
    }

    /**
     * Emit a log event from a message template and associated throwable object,
     * only if it passes the level check.
     *
     * @param level severity level of the log event
     * @param throwable a throwable object associated with this event
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
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

    /**
     * Emit a log event from a message template,
     * only if it passes the level check.
     *
     * @param level severity level of the log event
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    public suspend fun log(level: Level, template: String, vararg values: Any?) {
        if (!isLevelEnabled(level)) return
        if (values.isEmpty()) {
            emitEvent(level, null, template)
        } else {
            emitEvent(level, null, e(template, *values))
        }
    }

    /**
     * Emit a log event from a lambda and associated throwable object, only if it passes the level check.
     *
     * @param level severity level of the log event
     * @param throwable a throwable object associated with this event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun log(level: Level, throwable: Throwable, event: suspend Klogger.() -> Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, throwable, event())
    }

    /**
     * Emit a log event from a lambda, only if it passes the level check.
     *
     * @param level severity level of the log event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun log(level: Level, event: suspend Klogger.() -> Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, null, event())
    }

    /**
     * Log an event with a message and an explicit set of items.
     *
     * @param level level at which to log
     * @param message message to include in the log event
     * @param items items to include in the log event
     */
    public suspend fun log(level: Level, message: String, items: EventItems): Unit =
        emitEvent(level, null, message, items)

    /**
     * Log an event with an associated throwable, a message and an explicit set of items.
     *
     * @param level level at which to log
     * @param throwable throwable object associated with this log event
     * @param message message to include in the log event
     * @param items items to include in the log event
     */
    public suspend fun log(level: Level, throwable: Throwable, message: String, items: EventItems): Unit =
        emitEvent(level, throwable, message, items)

    /**
     * Emit a log event at [TRACE] level.
     *
     * @param event a representation of the event
     */
    public suspend fun trace(event: Any?): Unit =
        log(TRACE, event)

    /**
     * Emit a log event from a message template at [TRACE] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    public suspend fun trace(template: String, vararg values: Any?): Unit =
        log(TRACE, template, *values)

    /**
     * Emit a log event and associated throwable object at [TRACE] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event a representation of the event
     */
    public suspend fun trace(throwable: Throwable, event: Any?): Unit =
        log(WARN, throwable, event)

    /**
     * Emit a log event from a message template and associated throwable object at [TRACE] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param throwable a throwable object associated with this event
     * @param values values associated with holes in the message template
     */
    public suspend fun trace(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(TRACE, throwable, template, *values)

    /**
     * Emit a log event from a lambda at [TRACE] level.
     *
     * @param event lambda that returns a representation of the event
     */
    public suspend fun trace(event: suspend Klogger.() -> Any?): Unit =
        log(TRACE, event)

    /**
     * Emit a log event from a lambda and associated throwable object at [TRACE] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun trace(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(TRACE, throwable, event)

    /**
     * Emit a log event at [DEBUG] level.
     *
     * @param event a representation of the event
     */
    public suspend fun debug(event: Any?): Unit =
        log(DEBUG, event)

    /**
     * Emit a log event from a message template at [DEBUG] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    public suspend fun debug(template: String, vararg values: Any?): Unit =
        log(DEBUG, template, *values)

    /**
     * Emit a log event and associated throwable object at [DEBUG] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event a representation of the event
     */
    public suspend fun debug(throwable: Throwable, event: Any?): Unit =
        log(DEBUG, throwable, event)

    /**
     * Emit a log event from a message template and associated throwable object at [DEBUG] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param throwable a throwable object associated with this event
     * @param values values associated with holes in the message template
     */
    public suspend fun debug(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(DEBUG, throwable, template, *values)

    /**
     * Emit a log event from a lambda at [DEBUG] level.
     *
     * @param event lambda that returns a representation of the event
     */
    public suspend fun debug(event: suspend Klogger.() -> Any?): Unit =
        log(DEBUG, event)

    /**
     * Emit a log event from a lambda and associated throwable object at [DEBUG] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun debug(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(DEBUG, throwable, event)

    /**
     * Emit a log event at [INFO] level.
     *
     * @param event a representation of the event
     */
    public suspend fun info(event: Any?): Unit =
        log(INFO, event)

    /**
     * Emit a log event from a message template at [INFO] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    public suspend fun info(template: String, vararg values: Any?): Unit =
        log(INFO, template, *values)

    /**
     * Emit a log event and associated throwable object at [INFO] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event a representation of the event
     */
    public suspend fun info(throwable: Throwable, event: Any?): Unit =
        log(INFO, throwable, event)

    /**
     * Emit a log event from a message template and associated throwable object at [INFO] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param throwable a throwable object associated with this event
     * @param values values associated with holes in the message template
     */
    public suspend fun info(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(INFO, throwable, template, *values)

    /**
     * Emit a log event from a lambda at [INFO] level.
     *
     * @param event lambda that returns a representation of the event
     */
    public suspend fun info(event: suspend Klogger.() -> Any?): Unit =
        log(INFO, event)

    /**
     * Emit a log event from a lambda and associated throwable object at [INFO] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun info(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(INFO, throwable, event)

    /**
     * Emit a log event at [WARN] level.
     *
     * @param event a representation of the event
     */
    public suspend fun warn(event: Any?): Unit =
        log(WARN, event)

    /**
     * Emit a log event from a message template at [WARN] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    public suspend fun warn(template: String, vararg values: Any?): Unit =
        log(WARN, template, *values)

    /**
     * Emit a log event and associated throwable object at [WARN] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event a representation of the event
     */
    public suspend fun warn(throwable: Throwable, event: Any?): Unit =
        log(WARN, throwable, event)

    /**
     * Emit a log event from a message template and associated throwable object at [WARN] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param throwable a throwable object associated with this event
     * @param values values associated with holes in the message template
     */
    public suspend fun warn(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(WARN, throwable, template, *values)

    /**
     * Emit a log event from a lambda at [WARN] level.
     *
     * @param event lambda that returns a representation of the event
     */
    public suspend fun warn(event: suspend Klogger.() -> Any?): Unit =
        log(WARN, event)

    /**
     * Emit a log event from a lambda and associated throwable object at [WARN] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun warn(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(WARN, throwable, event)

    /**
     * Emit a log event at [ERROR] level.
     *
     * @param event a representation of the event
     */
    public suspend fun error(event: Any?): Unit =
        log(ERROR, event)

    /**
     * Emit a log event from a message template at [ERROR] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    public suspend fun error(template: String, vararg values: Any?): Unit =
        log(ERROR, template, *values)

    /**
     * Emit a log event and associated throwable object at [ERROR] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event a representation of the event
     */
    public suspend fun error(throwable: Throwable, event: Any?): Unit =
        log(ERROR, throwable, event)

    /**
     * Emit a log event from a message template and associated throwable object at [INFO] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param throwable a throwable object associated with this event
     * @param values values associated with holes in the message template
     */
    public suspend fun error(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(ERROR, throwable, template, *values)

    /**
     * Emit a log event from a lambda at [ERROR] level.
     *
     * @param event lambda that returns a representation of the event
     */
    public suspend fun error(event: suspend Klogger.() -> Any?): Unit =
        log(ERROR, event)

    /**
     * Emit a log event from a lambda and associated throwable object at [ERROR] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun error(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(ERROR, throwable, event)

    /**
     * Emit a log event at [FATAL] level.
     *
     * @param event a representation of the event
     */
    public suspend fun fatal(event: Any?): Unit =
        log(FATAL, event)

    /**
     * Emit a log event from a message template at [FATAL] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    public suspend fun fatal(template: String, vararg values: Any?): Unit =
        log(FATAL, template, *values)

    /**
     * Emit a log event and associated throwable object at [FATAL] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event a representation of the event
     */
    public suspend fun fatal(throwable: Throwable, event: Any?): Unit =
        log(FATAL, throwable, event)

    /**
     * Emit a log event from a message template and associated throwable object at [FATAL] level.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param throwable a throwable object associated with this event
     * @param values values associated with holes in the message template
     */
    public suspend fun fatal(throwable: Throwable, template: String, vararg values: Any?): Unit =
        log(FATAL, throwable, template, *values)

    /**
     * Emit a log event from a lambda at [FATAL] level.
     *
     * @param event lambda that returns a representation of the event
     */
    public suspend fun fatal(event: suspend Klogger.() -> Any?): Unit =
        log(FATAL, event)

    /**
     * Emit a log event from a lambda and associated throwable object at [FATAL] level.
     *
     * @param throwable a throwable object associated with this event
     * @param event lambda that returns a representation of the event
     */
    public suspend fun fatal(throwable: Throwable, event: suspend Klogger.() -> Any?): Unit =
        log(FATAL, throwable, event)

    /**
     * Emit a log event with a message and an explicit map of items at [TRACE] level.
     *
     * @param message the message
     * @param items a map of items to include in the log event
     */
    public suspend fun trace(message: String, items: EventItems): Unit = log(TRACE, message, items)

    /**
     * Emit a log event with a message and an explicit map of items at [DEBUG] level.
     *
     * @param message the message
     * @param items a map of items to include in the log event
     */
    public suspend fun debug(message: String, items: EventItems): Unit = log(DEBUG, message, items)

    /**
     * Emit a log event with a message and an explicit map of items at [INFO] level.
     *
     * @param message the message
     * @param items a map of items to include in the log event
     */
    public suspend fun info(message: String, items: EventItems): Unit = log(INFO, message, items)

    /**
     * Emit a log event with a message and an explicit map of items at [WARN] level.
     *
     * @param message the message
     * @param items a map of items to include in the log event
     */
    public suspend fun warn(message: String, items: EventItems): Unit = log(WARN, message, items)

    /**
     * Emit a log event with a message and an explicit map of items at [ERROR] level.
     *
     * @param message the message
     * @param items a map of items to include in the log event
     */
    public suspend fun error(message: String, items: EventItems): Unit = log(ERROR, message, items)

    /**
     * Emit a log event with a message and an explicit map of items at [FATAL] level.
     *
     * @param message the message
     * @param items a map of items to include in the log event
     */
    public suspend fun fatal(message: String, items: EventItems): Unit = log(FATAL, message, items)

    /**
     * Emit a log event with a message, associated throwable object and an explicit map of items at [TRACE] level.
     *
     * @param message the message
     * @param throwable a throwable object associated with this event
     * @param items a map of items to include in the log event
     */
    public suspend fun trace(message: String, throwable: Throwable, items: EventItems): Unit =
        log(TRACE, throwable, message, items)

    /**
     * Emit a log event with a message, associated throwable object and an explicit map of items at [DEBUG] level.
     *
     * @param message the message
     * @param throwable a throwable object associated with this event
     * @param items a map of items to include in the log event
     */
    public suspend fun debug(message: String, throwable: Throwable, items: EventItems): Unit =
        log(DEBUG, throwable, message, items)

    /**
     * Emit a log event with a message, associated throwable object and an explicit map of items at [INFO] level.
     *
     * @param message the message
     * @param throwable a throwable object associated with this event
     * @param items a map of items to include in the log event
     */
    public suspend fun info(message: String, throwable: Throwable, items: EventItems): Unit =
        log(INFO, throwable, message, items)

    /**
     * Emit a log event with a message, associated throwable object and an explicit map of items at [WARN] level.
     *
     * @param message the message
     * @param throwable a throwable object associated with this event
     * @param items a map of items to include in the log event
     */
    public suspend fun warn(message: String, throwable: Throwable, items: EventItems): Unit =
        log(WARN, throwable, message, items)

    /**
     * Emit a log event with a message, associated throwable object and an explicit map of items at [ERROR] level.
     *
     * @param message the message
     * @param throwable a throwable object associated with this event
     * @param items a map of items to include in the log event
     */
    public suspend fun error(message: String, throwable: Throwable, items: EventItems): Unit =
        log(ERROR, throwable, message, items)

    /**
     * Emit a log event with a message, associated throwable object and an explicit map of items at [FATAL] level.
     *
     * @param message the message
     * @param throwable a throwable object associated with this event
     * @param items a map of items to include in the log event
     */
    public suspend fun fatal(message: String, throwable: Throwable, items: EventItems): Unit =
        log(FATAL, throwable, message, items)

    /**
     * Evaluates a message template with the supplied values, returning an event.
     *
     * @param template a [message template](https://messagetemplates.org)
     * @param values values associated with holes in the message template
     */
    @Suppress("IDENTIFIER_LENGTH")
    public suspend fun e(template: String, vararg values: Any?): LogEvent
}
