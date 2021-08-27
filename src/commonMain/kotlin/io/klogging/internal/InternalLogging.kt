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

package io.klogging.internal

import io.klogging.Level
import io.klogging.Level.TRACE
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.WARN
import io.klogging.dispatching.STDERR
import io.klogging.dispatching.STDOUT
import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString
import io.klogging.rendering.localString

private const val KLOGGING_LOGGER = "Klogging"

/** Renderer specifically for internal logging. */
private val RENDER_INTERNAL: RenderString = { e: LogEvent ->
    val message =
        "${e.timestamp.localString} ${e.level} [${e.context}] : ${e.logger} : ${e.message}"
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
 * - Events are rendered to strings using [RENDER_INTERNAL]. They are printed directly
 *   to the standard output stream for [INFO] and lower level, and to the standard
 *   error stream for [WARN] and above levels.
 */
internal fun log(
    level: Level,
    message: String,
    throwable: Throwable? = null
) {
    if (level < KloggingState.kloggingMinLogLevel()) return
    val event = LogEvent(
        logger = KLOGGING_LOGGER,
        level = level,
        message = message,
        stackTrace = throwable?.stackTraceToString(),
    )
    if (level <= INFO) STDOUT(RENDER_INTERNAL(event))
    else STDERR(RENDER_INTERNAL(event))
}

internal fun trace(message: String, throwable: Throwable? = null) {
    log(TRACE, message, throwable)
}

internal fun debug(message: String, throwable: Throwable? = null) {
    log(DEBUG, message, throwable)
}

internal fun info(message: String, throwable: Throwable? = null) {
    log(INFO, message, throwable)
}

internal fun warn(message: String, throwable: Throwable? = null) {
    log(WARN, message, throwable)
}

internal fun error(message: String, throwable: Throwable? = null) {
    log(ERROR, message, throwable)
}
