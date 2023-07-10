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

package io.klogging.slf4j

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.NoCoLogger
import org.slf4j.MDC
import org.slf4j.helpers.MarkerIgnoringBase
import org.slf4j.helpers.MessageFormatter

/**
 * Implementation of [org.slf4j.Logger] that wraps an [io.klogging.NoCoLogger].
 *
 * It extends [MarkerIgnoringBase] because Klogging does not handle markers.
 */
public class NoCoLoggerWrapper(
    private val noCoLogger: NoCoLogger,
) : MarkerIgnoringBase() {

    override fun getName(): String = noCoLogger.name

    override fun isTraceEnabled(): Boolean = noCoLogger.isTraceEnabled()

    override fun trace(msg: String?) {
        emitEvent(TRACE, msg)
    }

    override fun trace(format: String?, arg: Any?) {
        if (format != null) emitEvent(TRACE, format, arg)
    }

    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) emitEvent(TRACE, format, arg1, arg2)
    }

    override fun trace(format: String?, vararg arguments: Any?) {
        if (format != null) emitEvent(TRACE, format, *arguments)
    }

    /**
     * Log an event with any kind of [Throwable].
     *
     * This function processes all combination of null-ness of the two arguments.
     */
    private fun logWithThrowable(level: Level, msg: String?, t: Throwable?) {
        if (msg != null) {
            if (t != null) emitEvent(level, t, msg) else emitEvent(level, msg)
        } else if (t != null) emitEvent(level, t)
    }

    override fun trace(msg: String?, t: Throwable?) {
        logWithThrowable(TRACE, msg, t)
    }

    override fun isDebugEnabled(): Boolean = noCoLogger.isDebugEnabled()

    override fun debug(msg: String?) {
        emitEvent(DEBUG, msg)
    }

    override fun debug(format: String?, arg: Any?) {
        if (format != null) emitEvent(DEBUG, format, arg)
    }

    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) emitEvent(DEBUG, format, arg1, arg2)
    }

    override fun debug(format: String?, vararg arguments: Any?) {
        if (format != null) emitEvent(DEBUG, format, *arguments)
    }

    override fun debug(msg: String?, t: Throwable?) {
        logWithThrowable(DEBUG, msg, t)
    }

    override fun isInfoEnabled(): Boolean = noCoLogger.isInfoEnabled()

    override fun info(msg: String?) {
        emitEvent(INFO, msg)
    }

    override fun info(format: String?, arg: Any?) {
        if (format != null) emitEvent(INFO, format, arg)
    }

    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) emitEvent(INFO, format, arg1, arg2)
    }

    override fun info(format: String?, vararg arguments: Any?) {
        if (format != null) emitEvent(INFO, format, *arguments)
    }

    override fun info(msg: String?, t: Throwable?) {
        logWithThrowable(INFO, msg, t)
    }

    override fun isWarnEnabled(): Boolean = noCoLogger.isWarnEnabled()

    override fun warn(msg: String?) {
        emitEvent(WARN, msg)
    }

    override fun warn(format: String?, arg: Any?) {
        if (format != null) emitEvent(WARN, format, arg)
    }

    override fun warn(format: String?, vararg arguments: Any?) {
        if (format != null) emitEvent(WARN, format, *arguments)
    }

    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) emitEvent(WARN, format, arg1, arg2)
    }

    override fun warn(msg: String?, t: Throwable?) {
        logWithThrowable(WARN, msg, t)
    }

    override fun isErrorEnabled(): Boolean = noCoLogger.isErrorEnabled()

    override fun error(msg: String?) {
        emitEvent(ERROR, msg)
    }

    override fun error(format: String?, arg: Any?) {
        if (format != null) emitEvent(ERROR, format, arg)
    }

    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) emitEvent(ERROR, format, arg1, arg2)
    }

    override fun error(format: String?, vararg arguments: Any?) {
        if (format != null) emitEvent(ERROR, format, *arguments)
    }

    override fun error(msg: String?, t: Throwable?) {
        logWithThrowable(ERROR, msg, t)
    }

    /**
     * Forward an event with context items from [MDC], handling null [format] and
     * absence of [arguments].
     */
    private fun emitEvent(level: Level, format: String?, vararg arguments: Any?) {
        val formatted = MessageFormatter.arrayFormat(format, arguments).message
        if (format == null || arguments.isEmpty()) {
            noCoLogger.emitEvent(level, null, formatted, contextItems())
        } else {
            noCoLogger.emitEvent(level, null, noCoLogger.e(formatted, *arguments), contextItems())
        }
    }

    /**
     * Forward an event and exception with context items from [MDC], handling null [format] and
     * absence of [arguments].
     */
    private fun emitEvent(
        level: Level,
        throwable: Throwable?,
        format: String? = null,
        vararg arguments: Any?,
    ) {
        val formatted = MessageFormatter.arrayFormat(format, arguments).message
        if (format == null || arguments.isEmpty()) {
            noCoLogger.emitEvent(level, throwable, formatted, contextItems())
        } else {
            noCoLogger.emitEvent(level, null, noCoLogger.e(formatted, *arguments), contextItems())
        }
    }

    private fun contextItems(): Map<String, Any?> = MDC.getCopyOfContextMap() ?: mapOf()
}
