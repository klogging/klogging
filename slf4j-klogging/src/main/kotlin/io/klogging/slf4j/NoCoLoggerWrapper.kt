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
 *
 * @param noCoLogger wrapper no-coroutine logger instance
 */
@Suppress("WRONG_OVERLOADING_FUNCTION_ARGUMENTS")
public class NoCoLoggerWrapper(
    private val noCoLogger: NoCoLogger,
) : LegacyAbstractLogger() {
    private val callerName = NoCoLoggerWrapper::class.java.name

    /**
     * Name of the logger.
     *
     * @return logger name
     */
    public override fun getName(): String = noCoLogger.name

    /**
     * Is the logger enabled at SL4J [org.slf4j.event.Level.TRACE] level?
     *
     * @return `true` if enabled at Klogging [TRACE] level
     */
    public override fun isTraceEnabled(): Boolean = noCoLogger.isTraceEnabled()

    /**
     * Log an event at [org.slf4j.event.Level.TRACE] level.
     *
     * @param message the message
     */
    public override fun trace(message: String?) {
        emitEvent(TRACE, message)
    }

    /**
     * Log an event from format and an argument at [org.slf4j.event.Level.TRACE] level.
     *
     * @param format the format string
     * @param argument the argument
     */
    public override fun trace(format: String?, argument: Any?) {
        format?.let {
            emitEvent(TRACE, format, argument)
        }
    }

    /**
     * Log an event from format and two arguments at [org.slf4j.event.Level.TRACE] level.
     *
     * @param format the format string
     * @param argument1 first argument
     * @param argument2 second argument
     */
    public override fun trace(
        format: String?,
        argument1: Any?,
        argument2: Any?,
    ) {
        format?.let {
            emitEvent(TRACE, format, argument1, argument2)
        }
    }

    /**
     * Log an event from format and three or more arguments at [org.slf4j.event.Level.TRACE] level.
     *
     * @param format the format string
     * @param arguments list of three or more arguments
     */
    public override fun trace(format: String?, vararg arguments: Any?) {
        format?.let {
            emitEvent(TRACE, format, *arguments)
        }
    }

    /**
     * Log an event and associated throwable object at [org.slf4j.event.Level.TRACE] level.
     *
     * @param message the message
     */
    public override fun trace(message: String?, throwable: Throwable?) {
        logWithThrowable(TRACE, message, throwable)
    }

    /**
     * Log an event with any kind of throwable object.
     *
     * This function processes all combinations of null-ness of the two arguments.
     *
     * @param level Klogging [Level] at which to log
     * @param message message to log
     * @param throwable a throwable object associated with the log event
     */
    private fun logWithThrowable(
        level: Level,
        message: String?,
        throwable: Throwable?,
    ) {
        message?.let {
            throwable?.let {
                emitEvent(level, throwable, message)
            } ?: emitEvent(level, message)
        } ?: throwable?.let {
            emitEvent(level, throwable)
        }
    }

    /**
     * Is the logger enabled at SL4J [org.slf4j.event.Level.DEBUG] level?
     *
     * @return `true` if enabled at Klogging [DEBUG] level
     */
    public override fun isDebugEnabled(): Boolean = noCoLogger.isDebugEnabled()

    /**
     * Log an event at [org.slf4j.event.Level.DEBUG] level.
     *
     * @param message the message
     */
    public override fun debug(message: String?) {
        emitEvent(DEBUG, message)
    }

    /**
     * Log an event from format and an argument at [org.slf4j.event.Level.DEBUG] level.
     *
     * @param format the format string
     * @param argument the argument
     */
    public override fun debug(format: String?, argument: Any?) {
        format?.let { emitEvent(DEBUG, it, argument) }
    }

    /**
     * Log an event from format and two arguments at [org.slf4j.event.Level.DEBUG] level.
     *
     * @param format the format string
     * @param argument1 first argument
     * @param argument2 second argument
     */
    public override fun debug(
        format: String?,
        argument1: Any?,
        argument2: Any?,
    ) {
        format?.let { emitEvent(DEBUG, it, argument1, argument2) }
    }

    /**
     * Log an event from format and three or more arguments at [org.slf4j.event.Level.DEBUG] level.
     *
     * @param format the format string
     * @param arguments list of three or more arguments
     */
    public override fun debug(format: String?, vararg arguments: Any?) {
        format?.let { emitEvent(DEBUG, it, arguments) }
    }

    /**
     * Log an event and associated throwable object at [org.slf4j.event.Level.DEBUG] level.
     *
     * @param message the message
     */
    public override fun debug(message: String?, throwable: Throwable?) {
        logWithThrowable(DEBUG, message, throwable)
    }

    /**
     * Is the logger enabled at SL4J [org.slf4j.event.Level.INFO] level?
     *
     * @return `true` if enabled at Klogging [INFO] level
     */
    public override fun isInfoEnabled(): Boolean = noCoLogger.isInfoEnabled()

    /**
     * Log an event at [org.slf4j.event.Level.INFO] level.
     *
     * @param message the message
     */
    public override fun info(message: String?) {
        emitEvent(INFO, message)
    }

    /**
     * Log an event from format and an argument at [org.slf4j.event.Level.INFO] level.
     *
     * @param format the format string
     * @param argument the argument
     */
    public override fun info(format: String?, argument: Any?) {
        format?.let { emitEvent(INFO, it, argument) }
    }

    /**
     * Log an event from format and two arguments at [org.slf4j.event.Level.INFO] level.
     *
     * @param format the format string
     * @param argument1 first argument
     * @param argument2 second argument
     */
    public override fun info(
        format: String?,
        argument1: Any?,
        argument2: Any?,
    ) {
        format?.let { emitEvent(INFO, it, argument1, argument2) }
    }

    /**
     * Log an event from format and three or more arguments at [org.slf4j.event.Level.INFO] level.
     *
     * @param format the format string
     * @param arguments list of three or more arguments
     */
    public override fun info(format: String?, vararg arguments: Any?) {
        format?.let { emitEvent(INFO, it, *arguments) }
    }

    /**
     * Log an event and associated throwable object at [org.slf4j.event.Level.INFO] level.
     *
     * @param message the message
     */
    public override fun info(message: String?, throwable: Throwable?) {
        logWithThrowable(INFO, message, throwable)
    }

    /**
     * Is the logger enabled at SL4J [org.slf4j.event.Level.WARN] level?
     *
     * @return `true` if enabled at Klogging [WARN] level
     */
    public override fun isWarnEnabled(): Boolean = noCoLogger.isWarnEnabled()

    /**
     * Log an event at [org.slf4j.event.Level.WARN] level.
     *
     * @param message the message
     */
    public override fun warn(message: String?) {
        emitEvent(WARN, message)
    }

    /**
     * Log an event from format and an argument at [org.slf4j.event.Level.WARN] level.
     *
     * @param format the format string
     * @param argument the argument
     */
    public override fun warn(format: String?, argument: Any?) {
        format?.let {
            emitEvent(WARN, format, argument)
        }
    }

    /**
     * Log an event from format and two arguments at [org.slf4j.event.Level.WARN] level.
     *
     * @param format the format string
     * @param argument1 first argument
     * @param argument2 second argument
     */
    public override fun warn(
        format: String?,
        argument1: Any?,
        argument2: Any?,
    ) {
        format?.let {
            emitEvent(WARN, format, argument1, argument2)
        }
    }

    /**
     * Log an event from format and three or more arguments at [org.slf4j.event.Level.WARN] level.
     *
     * @param format the format string
     * @param arguments list of three or more arguments
     */
    public override fun warn(format: String?, vararg arguments: Any?) {
        format?.let {
            emitEvent(WARN, format, *arguments)
        }
    }

    /**
     * Log an event and associated throwable object at [org.slf4j.event.Level.WARN] level.
     *
     * @param message the message
     */
    public override fun warn(message: String?, throwable: Throwable?) {
        logWithThrowable(WARN, message, throwable)
    }

    /**
     * Is the logger enabled at SL4J [org.slf4j.event.Level.ERROR] level?
     *
     * @return `true` if enabled at Klogging [ERROR] level
     */
    public override fun isErrorEnabled(): Boolean = noCoLogger.isErrorEnabled()

    /**
     * Log an event at [org.slf4j.event.Level.ERROR] level.
     *
     * @param message the message
     */
    public override fun error(message: String?) {
        emitEvent(ERROR, message)
    }

    /**
     * Log an event from format and an argument at [org.slf4j.event.Level.ERROR] level.
     *
     * @param format the format string
     * @param argument the argument
     */
    public override fun error(format: String?, argument: Any?) {
        format?.let { emitEvent(ERROR, it, argument) }
    }

    /**
     * Log an event from format and two arguments at [org.slf4j.event.Level.ERROR] level.
     *
     * @param format the format string
     * @param argument1 first argument
     * @param argument2 second argument
     */
    public override fun error(
        format: String?,
        argument1: Any?,
        argument2: Any?,
    ) {
        format?.let { emitEvent(ERROR, it, argument1, argument2) }
    }

    /**
     * Log an event from format and three or more arguments at [org.slf4j.event.Level.ERROR] level.
     *
     * @param format the format string
     * @param arguments list of three or more arguments
     */
    public override fun error(format: String?, vararg arguments: Any?) {
        format?.let { emitEvent(ERROR, it, arguments) }
    }

    /**
     * Log an event and associated throwable object at [org.slf4j.event.Level.ERROR] level.
     *
     * @param message the message
     */
    public override fun error(message: String?, throwable: Throwable?) {
        logWithThrowable(ERROR, message, throwable)
    }

    /**
     * Return the fully-qualified caller name.
     *
     * @return fully-qualified name of this class
     */
    public override fun getFullyQualifiedCallerName(): String? = callerName

    /**
     * Process the logging call after all processing.
     *
     * @param level SLF4J level of the log event
     * @param marker any marker associated with the log event
     * @param messagePattern formatted message pattern
     * @param arguments arguments to the formatted message
     * @param throwable throwable objects associated with the log event
     */
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
    private fun emitEvent(
        level: Level,
        format: String?,
        vararg arguments: Any?,
    ) {
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

/**
 * Map SLF4J severity level to the corresponding Klogging level.
 *
 * @param slf4jLevel SLF4J severity level
 * @return the corresponding Klogging level
 */
internal fun kloggingLevel(slf4jLevel: org.slf4j.event.Level?): Level = when (slf4jLevel) {
    org.slf4j.event.Level.TRACE -> TRACE
    org.slf4j.event.Level.DEBUG -> DEBUG
    org.slf4j.event.Level.INFO -> INFO
    org.slf4j.event.Level.WARN -> WARN
    org.slf4j.event.Level.ERROR -> ERROR
    else -> NONE
}
