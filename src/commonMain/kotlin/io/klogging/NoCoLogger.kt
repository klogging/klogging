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
 * Interface for logging from outside coroutines.
 *
 * `NoCoLogger` is a working name for now.
 */
public interface NoCoLogger : BaseLogger {

    public fun emitEvent(level: Level, exception: Exception?, event: Any?)

    public fun log(level: Level, exception: Exception, event: Any?): Unit =
        emitEvent(level, exception, event)

    public fun log(level: Level, event: Any?): Unit = emitEvent(level, null, event)

    public fun log(
        level: Level,
        exception: Exception,
        template: String,
        vararg values: Any?
    ): Unit =
        if (values.isEmpty()) emitEvent(level, exception, template)
        else emitEvent(level, exception, e(template, *values))

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

    public fun trace(exception: Exception, event: Any?): Unit = log(TRACE, exception, event)
    public fun debug(exception: Exception, event: Any?): Unit = log(DEBUG, exception, event)
    public fun info(exception: Exception, event: Any?): Unit = log(INFO, exception, event)
    public fun warn(exception: Exception, event: Any?): Unit = log(WARN, exception, event)
    public fun error(exception: Exception, event: Any?): Unit = log(ERROR, exception, event)
    public fun fatal(exception: Exception, event: Any?): Unit = log(FATAL, exception, event)

    public fun trace(exception: Exception, template: String, vararg values: Any?): Unit =
        log(TRACE, exception, template, *values)

    public fun debug(exception: Exception, template: String, vararg values: Any?): Unit =
        log(DEBUG, exception, template, *values)

    public fun info(exception: Exception, template: String, vararg values: Any?): Unit =
        log(INFO, exception, template, *values)

    public fun warn(exception: Exception, template: String, vararg values: Any?): Unit =
        log(WARN, exception, template, *values)

    public fun error(exception: Exception, template: String, vararg values: Any?): Unit =
        log(ERROR, exception, template, *values)

    public fun fatal(exception: Exception, template: String, vararg values: Any?): Unit =
        log(FATAL, exception, template, *values)

    public fun log(level: Level, exception: Exception, event: NoCoLogger.() -> Any?) {
        if (isLevelEnabled(level)) emitEvent(level, exception, event())
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

    public fun trace(exception: Exception, event: NoCoLogger.() -> Any?): Unit =
        log(TRACE, exception, event)

    public fun debug(exception: Exception, event: NoCoLogger.() -> Any?): Unit =
        log(DEBUG, exception, event)

    public fun info(exception: Exception, event: NoCoLogger.() -> Any?): Unit =
        log(INFO, exception, event)

    public fun warn(exception: Exception, event: NoCoLogger.() -> Any?): Unit =
        log(WARN, exception, event)

    public fun error(exception: Exception, event: NoCoLogger.() -> Any?): Unit =
        log(ERROR, exception, event)

    public fun fatal(exception: Exception, event: NoCoLogger.() -> Any?): Unit =
        log(FATAL, exception, event)

    /**
     * Evaluates a message template with the supplied values, returning [LogEvent].
     */
    public fun e(template: String, vararg values: Any?): LogEvent
}
