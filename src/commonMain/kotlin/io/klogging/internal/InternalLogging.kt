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

import io.klogging.dispatching.STDERR
import io.klogging.dispatching.STDOUT
import io.klogging.events.Level
import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString
import io.klogging.rendering.localString

internal val RENDER_INTERNAL: RenderString = { e: LogEvent ->
    "${e.timestamp.localString} ${e.level} : ${e.message}" +
        if (e.items.isNotEmpty()) " : ${e.items}" else "" +
            if (e.stackTrace != null) "\n${e.stackTrace}" else ""
}

/**
 * Simplified internal logging for Klogging diagnostics, especially during
 * configuration.
 */
public fun log(loggerName: String, level: Level, message: String, exception: Exception? = null) {
    val event = LogEvent(
        logger = loggerName,
        level = level,
        message = message,
        stackTrace = exception?.stackTraceToString(),
    )
    if (level <= Level.INFO) STDOUT(RENDER_INTERNAL(event))
    else STDERR(RENDER_INTERNAL(event))
}

public fun debug(loggerName: String, message: String, exception: Exception? = null) {
    log(loggerName, Level.DEBUG, message, exception)
}

public fun info(loggerName: String, message: String, exception: Exception? = null) {
    log(loggerName, Level.INFO, message, exception)
}

public fun warn(loggerName: String, message: String, exception: Exception? = null) {
    log(loggerName, Level.WARN, message, exception)
}

public fun error(loggerName: String, message: String, exception: Exception? = null) {
    log(loggerName, Level.ERROR, message, exception)
}
