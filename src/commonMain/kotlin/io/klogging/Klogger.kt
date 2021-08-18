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

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent

/**
 * Logger interface for sending log events inside coroutines.
 */
public interface Klogger : BaseLogger {

    /** Emit an event after minimum level checking. */
    public suspend fun emitEvent(level: Level, exception: Exception?, event: Any?)

    public suspend fun log(level: Level, exception: Exception, event: Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, exception, event)
    }

    public suspend fun log(level: Level, event: Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, null, event)
    }

    public suspend fun log(
        level: Level,
        exception: Exception,
        template: String,
        vararg values: Any?
    ) {
        if (!isLevelEnabled(level)) return
        if (values.isEmpty()) emitEvent(level, exception, template)
        else emitEvent(level, exception, e(template, *values))
    }

    public suspend fun log(level: Level, template: String, vararg values: Any?) {
        if (!isLevelEnabled(level)) return
        if (values.isEmpty()) emitEvent(level, null, template)
        else emitEvent(level, null, e(template, *values))
    }

    public suspend fun log(level: Level, exception: Exception, event: suspend Klogger.() -> Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, exception, event())
    }

    public suspend fun log(level: Level, event: suspend Klogger.() -> Any?) {
        if (!isLevelEnabled(level)) return
        emitEvent(level, null, event())
    }

    public suspend fun trace(event: Any?): Unit = log(TRACE, event)
    public suspend fun trace(template: String, vararg values: Any?): Unit =
        log(TRACE, template, *values)

    public suspend fun trace(exception: Exception, event: Any?): Unit =
        log(WARN, exception, event)

    public suspend fun trace(exception: Exception, template: String, vararg values: Any?): Unit =
        log(TRACE, exception, template, *values)

    public suspend fun trace(event: suspend Klogger.() -> Any?): Unit =
        log(TRACE, event)

    public suspend fun trace(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(TRACE, exception, event)

    public suspend fun debug(event: Any?): Unit = log(DEBUG, event)
    public suspend fun debug(template: String, vararg values: Any?): Unit =
        log(DEBUG, template, *values)

    public suspend fun debug(exception: Exception, event: Any?): Unit =
        log(DEBUG, exception, event)

    public suspend fun debug(exception: Exception, template: String, vararg values: Any?): Unit =
        log(DEBUG, exception, template, *values)

    public suspend fun debug(event: suspend Klogger.() -> Any?): Unit =
        log(DEBUG, event)

    public suspend fun debug(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(DEBUG, exception, event)

    public suspend fun info(event: Any?): Unit = log(INFO, event)
    public suspend fun info(template: String, vararg values: Any?): Unit =
        log(INFO, template, *values)

    public suspend fun info(exception: Exception, event: Any?): Unit =
        log(INFO, exception, event)

    public suspend fun info(exception: Exception, template: String, vararg values: Any?): Unit =
        log(INFO, exception, template, *values)

    public suspend fun info(event: suspend Klogger.() -> Any?): Unit =
        log(INFO, event)

    public suspend fun info(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(INFO, exception, event)

    public suspend fun warn(event: Any?): Unit = log(WARN, event)
    public suspend fun warn(template: String, vararg values: Any?): Unit =
        log(WARN, template, *values)

    public suspend fun warn(exception: Exception, event: Any?): Unit =
        log(WARN, exception, event)

    public suspend fun warn(exception: Exception, template: String, vararg values: Any?): Unit =
        log(WARN, exception, template, *values)

    public suspend fun warn(event: suspend Klogger.() -> Any?): Unit =
        log(WARN, event)

    public suspend fun warn(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(WARN, exception, event)

    public suspend fun error(event: Any?): Unit = log(ERROR, event)
    public suspend fun error(template: String, vararg values: Any?): Unit =
        log(ERROR, template, *values)

    public suspend fun error(exception: Exception, event: Any?): Unit =
        log(ERROR, exception, event)

    public suspend fun error(exception: Exception, template: String, vararg values: Any?): Unit =
        log(ERROR, exception, template, *values)

    public suspend fun error(event: suspend Klogger.() -> Any?): Unit =
        log(ERROR, event)

    public suspend fun error(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(ERROR, exception, event)

    public suspend fun fatal(event: Any?): Unit = log(FATAL, event)

    public suspend fun fatal(template: String, vararg values: Any?): Unit =
        log(FATAL, template, *values)

    public suspend fun fatal(exception: Exception, event: Any?): Unit =
        log(FATAL, exception, event)

    public suspend fun fatal(exception: Exception, template: String, vararg values: Any?): Unit =
        log(FATAL, exception, template, *values)

    public suspend fun fatal(event: suspend Klogger.() -> Any?): Unit =
        log(FATAL, event)

    public suspend fun fatal(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public suspend fun e(template: String, vararg values: Any?): LogEvent
}
