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

import io.klogging.events.LogEvent

/**
 * Logger interface for sending log events inside coroutines.
 */
public interface Klogger : BaseLogger {

    public suspend fun emitEvent(level: Level, exception: Exception?, event: Any?)

    public suspend fun log(level: Level, exception: Exception, event: Any?): Unit = emitEvent(level, exception, event)
    public suspend fun log(level: Level, event: Any?): Unit = emitEvent(level, null, event)

    public suspend fun log(level: Level, exception: Exception, template: String, vararg values: Any?): Unit =
        if (values.isEmpty()) emitEvent(level, exception, template)
        else emitEvent(level, exception, e(template, *values))

    public suspend fun log(level: Level, template: String, vararg values: Any?): Unit =
        if (values.isEmpty()) emitEvent(level, null, template)
        else emitEvent(level, null, e(template, *values))

    public suspend fun trace(event: Any?): Unit = log(Level.TRACE, event)
    public suspend fun debug(event: Any?): Unit = log(Level.DEBUG, event)
    public suspend fun info(event: Any?): Unit = log(Level.INFO, event)
    public suspend fun warn(event: Any?): Unit = log(Level.WARN, event)
    public suspend fun error(event: Any?): Unit = log(Level.ERROR, event)
    public suspend fun fatal(event: Any?): Unit = log(Level.FATAL, event)

    public suspend fun trace(template: String, vararg values: Any?): Unit = log(Level.TRACE, template, *values)
    public suspend fun debug(template: String, vararg values: Any?): Unit = log(Level.DEBUG, template, *values)
    public suspend fun info(template: String, vararg values: Any?): Unit = log(Level.INFO, template, *values)
    public suspend fun warn(template: String, vararg values: Any?): Unit = log(Level.WARN, template, *values)
    public suspend fun error(template: String, vararg values: Any?): Unit = log(Level.ERROR, template, *values)
    public suspend fun fatal(template: String, vararg values: Any?): Unit = log(Level.FATAL, template, *values)

    public suspend fun trace(exception: Exception, event: Any?): Unit = log(Level.WARN, exception, event)
    public suspend fun debug(exception: Exception, event: Any?): Unit = log(Level.DEBUG, exception, event)
    public suspend fun info(exception: Exception, event: Any?): Unit = log(Level.INFO, exception, event)
    public suspend fun warn(exception: Exception, event: Any?): Unit = log(Level.WARN, exception, event)
    public suspend fun error(exception: Exception, event: Any?): Unit = log(Level.ERROR, exception, event)
    public suspend fun fatal(exception: Exception, event: Any?): Unit = log(Level.FATAL, exception, event)

    public suspend fun trace(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.TRACE, exception, template, *values)

    public suspend fun debug(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.DEBUG, exception, template, *values)

    public suspend fun info(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.INFO, exception, template, *values)

    public suspend fun warn(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.WARN, exception, template, *values)

    public suspend fun error(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.ERROR, exception, template, *values)

    public suspend fun fatal(exception: Exception, template: String, vararg values: Any?): Unit =
        log(Level.FATAL, exception, template, *values)

    public suspend fun log(level: Level, exception: Exception, event: suspend Klogger.() -> Any?) {
        if (isLevelEnabled(level)) emitEvent(level, exception, event())
    }

    public suspend fun log(level: Level, event: suspend Klogger.() -> Any?) {
        if (isLevelEnabled(level)) emitEvent(level, null, event())
    }

    public suspend fun trace(event: suspend Klogger.() -> Any?): Unit = log(Level.TRACE, event)
    public suspend fun debug(event: suspend Klogger.() -> Any?): Unit = log(Level.DEBUG, event)
    public suspend fun info(event: suspend Klogger.() -> Any?): Unit = log(Level.INFO, event)
    public suspend fun warn(event: suspend Klogger.() -> Any?): Unit = log(Level.WARN, event)
    public suspend fun error(event: suspend Klogger.() -> Any?): Unit = log(Level.ERROR, event)
    public suspend fun fatal(event: suspend Klogger.() -> Any?): Unit = log(Level.FATAL, event)

    public suspend fun trace(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.TRACE, exception, event)

    public suspend fun debug(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.DEBUG, exception, event)

    public suspend fun info(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.INFO, exception, event)

    public suspend fun warn(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.WARN, exception, event)

    public suspend fun error(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.ERROR, exception, event)

    public suspend fun fatal(exception: Exception, event: suspend Klogger.() -> Any?): Unit =
        log(Level.FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public suspend fun e(template: String, vararg values: Any?): LogEvent
}
