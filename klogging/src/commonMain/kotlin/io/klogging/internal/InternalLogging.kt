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

package io.klogging.internal

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import io.klogging.rendering.RENDER_ANSI

/**
 * Internal logging for Klogging diagnostics. It uses a [LogEvent] with these simplifications:
 *
 * - Structured logging is not available. It can be called only with message strings
 *   and an optional error or exception.
 *
 * - Events are rendered to strings using [RENDER_ANSI]. They are printed directly
 *   to the standard output stream for [INFO] and lower level, and to the standard
 *   error stream for [WARN] and above levels.
 *
 *  @param logger name of the logger
 *  @param level level to log at
 *  @param message log message
 *  @param throwable possible [Throwable] associated with the log message
 */
@Suppress("DEBUG_PRINT")
internal fun log(
    logger: String,
    level: Level,
    message: String,
    throwable: Throwable? = null,
) {
    if (level < KloggingEngine.kloggingMinLogLevel()) return
    val event = LogEvent(
        logger = logger,
        level = level,
        message = message,
        stackTrace = throwable?.stackTraceToString(),
    )
    if (level <= INFO) {
        println(RENDER_ANSI(event))
    } else {
        printErr(RENDER_ANSI(event))
    }
}

/**
 * Print a message to the error channel.
 * @param message message to print
 */
internal expect fun printErr(message: String)

/**
 * Print a log message at [TRACE] level.
 * @param logger name of the logger
 * @param message message to print
 * @param throwable possible [Throwable] associated with the message
 */
internal fun trace(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, TRACE, message, throwable)
}

/**
 * Print a log message at [DEBUG] level.
 * @param logger name of the logger
 * @param message message to print
 * @param throwable possible [Throwable] associated with the message
 */
internal fun debug(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, DEBUG, message, throwable)
}

/**
 * Print a log message at [INFO] level.
 * @param logger name of the logger
 * @param message message to print
 * @param throwable possible [Throwable] associated with the message
 */
internal fun info(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, INFO, message, throwable)
}

/**
 * Print a log message at [WARN] level.
 * @param logger name of the logger
 * @param message message to print
 * @param throwable possible [Throwable] associated with the message
 */
internal fun warn(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, WARN, message, throwable)
}

/**
 * Print a log message at [ERROR] level.
 * @param logger name of the logger
 * @param message message to print
 * @param throwable possible [Throwable] associated with the message
 */
internal fun error(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, ERROR, message, throwable)
}
