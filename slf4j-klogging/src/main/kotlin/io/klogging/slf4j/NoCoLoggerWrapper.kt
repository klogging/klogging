/*

   Copyright 2021-2024 Michael Strasser.

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
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.NoCoLogger
import org.slf4j.MDC
import org.slf4j.Marker
import org.slf4j.helpers.LegacyAbstractLogger
import org.slf4j.helpers.MessageFormatter

/**
 * Implementation of [org.slf4j.Logger] that wraps an instance of [io.klogging.NoCoLogger].
 *
 * Klogging does not handle markers.
 */
@Suppress("WRONG_OVERLOADING_FUNCTION_ARGUMENTS")
public class NoCoLoggerWrapper(
    private val noCoLogger: NoCoLogger,
) : LegacyAbstractLogger() {

    private val self = NoCoLoggerWrapper::class.java.name

    public override fun getName(): String = noCoLogger.name

    public override fun isTraceEnabled(): Boolean = noCoLogger.isTraceEnabled()

    public override fun trace(message: String?) {
        emitEvent(TRACE, message)
    }

    public override fun trace(format: String?, argument: Any?) {
        if (format != null) emitEvent(TRACE, format, argument)
    }

    public override fun trace(format: String?, argument1: Any?, argument2: Any?) {
        if (format != null) emitEvent(TRACE, format, argument1, argument2)
    }

    public override fun trace(format: String?, vararg arguments: Any?) {
        if (format != null) emitEvent(TRACE, format, *arguments)
    }

    /**
     * Log an event with any kind of [Throwable].
     *
     * This function processes all combination of null-ness of the two arguments.
     */
    private fun logWithThrowable(level: Level, message: String?, throwable: Throwable?) {
        message?.let {
            throwable?.let {
                emitEvent(level, throwable, message)
            } ?: emitEvent(level, message)
        } ?: throwable?.let {
            emitEvent(level, throwable)
        }
    }

    public override fun trace(message: String?, throwable: Throwable?) {
        logWithThrowable(TRACE, message, throwable)
    }

    public override fun isDebugEnabled(): Boolean = noCoLogger.isDebugEnabled()

    public override fun debug(message: String?) {
        emitEvent(DEBUG, message)
    }

    public override fun debug(format: String?, argument: Any?) {
        format?.let { emitEvent(DEBUG, it, argument) }
    }

    public override fun debug(format: String?, argument1: Any?, argument2: Any?) {
        format?.let { emitEvent(DEBUG, it, argument1, argument2) }
    }

    public override fun debug(format: String?, vararg arguments: Any?) {
        format?.let { emitEvent(DEBUG, it, arguments) }
    }

    public override fun debug(message: String?, throwable: Throwable?) {
        logWithThrowable(DEBUG, message, throwable)
    }

    public override fun isInfoEnabled(): Boolean = noCoLogger.isInfoEnabled()

    public override fun info(message: String?) {
        emitEvent(INFO, message)
    }

    public override fun info(format: String?, argument: Any?) {
        format?.let { emitEvent(INFO, it, argument) }
    }

    public override fun info(format: String?, argument1: Any?, argument2: Any?) {
        format?.let { emitEvent(INFO, it, argument1, argument2) }
    }

    public override fun info(format: String?, vararg arguments: Any?) {
        format?.let { emitEvent(INFO, it, arguments) }
    }

    public override fun info(message: String?, throwable: Throwable?) {
        logWithThrowable(INFO, message, throwable)
    }

    public override fun isWarnEnabled(): Boolean = noCoLogger.isWarnEnabled()

    public override fun warn(message: String?) {
        emitEvent(WARN, message)
    }

    public override fun warn(format: String?, argument: Any?) {
        if (format != null) emitEvent(WARN, format, argument)
    }

    public override fun warn(format: String?, vararg arguments: Any?) {
        if (format != null) emitEvent(WARN, format, *arguments)
    }

    public override fun warn(format: String?, argument1: Any?, argument2: Any?) {
        if (format != null) emitEvent(WARN, format, argument1, argument2)
    }

    public override fun warn(message: String?, throwable: Throwable?) {
        logWithThrowable(WARN, message, throwable)
    }

    public override fun isErrorEnabled(): Boolean = noCoLogger.isErrorEnabled()

    public override fun error(message: String?) {
        emitEvent(ERROR, message)
    }

    public override fun error(format: String?, argument: Any?) {
        format?.let { emitEvent(ERROR, it, argument) }
    }

    public override fun error(format: String?, argument1: Any?, argument2: Any?) {
        format?.let { emitEvent(ERROR, it, argument1, argument2) }
    }

    public override fun error(format: String?, vararg arguments: Any?) {
        format?.let { emitEvent(ERROR, it, arguments) }
    }

    public override fun error(message: String?, throwable: Throwable?) {
        logWithThrowable(ERROR, message, throwable)
    }

    public override fun getFullyQualifiedCallerName(): String? = self

    public override fun handleNormalizedLoggingCall(
        level: org.slf4j.event.Level?,
        marker: Marker?,
        messagePattern: String?,
        arguments: Array<out Any>?,
        throwable: Throwable?,
    ) {
        emitEvent(kloggingLevel(level), throwable, messagePattern, arguments)
    }

    /**
     * Forward an event with context items from [MDC], handling null [format] and
     * absence of [arguments].
     */
    private fun emitEvent(level: Level, format: String?, vararg arguments: Any?) {
        val formatted = MessageFormatter.arrayFormat(format, arguments).message
        if (format == null || arguments.isEmpty()) {
            noCoLogger.emitEvent(level, null, formatted)
        } else {
            noCoLogger.emitEvent(level, null, noCoLogger.e(formatted, *arguments))
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
            noCoLogger.emitEvent(level, throwable, formatted)
        } else {
            noCoLogger.emitEvent(level, null, noCoLogger.e(formatted, *arguments))
        }
    }
}

internal fun kloggingLevel(slf4jLevel: org.slf4j.event.Level?): Level = when (slf4jLevel) {
    org.slf4j.event.Level.TRACE -> TRACE
    org.slf4j.event.Level.DEBUG -> DEBUG
    org.slf4j.event.Level.INFO -> INFO
    org.slf4j.event.Level.WARN -> WARN
    org.slf4j.event.Level.ERROR -> ERROR
    else -> NONE
}
