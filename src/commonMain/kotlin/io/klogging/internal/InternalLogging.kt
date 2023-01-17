/*

   Copyright 2021-2022 Michael Strasser.

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

package io.klogging.internal

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString
import io.klogging.rendering.colour5
import io.klogging.rendering.localTime
import io.klogging.rendering.rightAlign

/** Renderer specifically for internal logging. */
private val renderInternal: RenderString = { e: LogEvent ->
    val message = "${e.timestamp.localTime} ${e.level.colour5} [${e.context?.rightAlign(20)}]" +
        " : ${e.logger.rightAlign(20)} : ${e.message}"
    val maybeItems = if (e.items.isNotEmpty()) " : ${e.items}" else ""
    val maybeStackTrace = if (e.stackTrace != null) "\n${e.stackTrace}" else ""
    message + maybeItems + maybeStackTrace
}

/**
 * Internal logging for Klogging diagnostics. It uses a [LogEvent] with these simplifications:
 *
 * - Structured logging is not available. It can be called only with message strings
 *   and an optional error or exception.
 *
 * - Events are rendered to strings using [renderInternal]. They are printed directly
 *   to the standard output stream for [INFO] and lower level, and to the standard
 *   error stream for [WARN] and above levels.
 */
internal fun log(
    logger: String,
    level: Level,
    message: String,
    throwable: Throwable? = null
) {
    if (level < KloggingEngine.kloggingMinLogLevel()) return
    val event = LogEvent(
        logger = logger,
        level = level,
        message = message,
        stackTrace = throwable?.stackTraceToString()
    )
    if (level <= INFO) {
        println(renderInternal(event))
    } else {
        printErr(renderInternal(event))
    }
}

internal expect fun printErr(message: String): Unit

internal fun trace(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, TRACE, message, throwable)
}

internal fun debug(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, DEBUG, message, throwable)
}

internal fun info(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, INFO, message, throwable)
}

internal fun warn(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, WARN, message, throwable)
}

internal fun error(logger: String, message: String, throwable: Throwable? = null) {
    log(logger, ERROR, message, throwable)
}
