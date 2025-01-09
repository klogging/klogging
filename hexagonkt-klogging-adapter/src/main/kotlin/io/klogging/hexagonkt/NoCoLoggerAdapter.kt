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

package io.klogging.hexagonkt

import com.hexagonkt.core.logging.LoggerPort
import com.hexagonkt.core.logging.LoggingLevel
import io.klogging.NoCoLogger

/**
 * Klogging implementation of Hexagon [LoggerPort] to wrap a [NoCoLogger] to emit and
 * send log events.
 *
 * @param noCoLogger
 */
internal class NoCoLoggerAdapter(private val noCoLogger: NoCoLogger) : LoggerPort {
    /**
     * Emit and send a log message with the specified level.
     */
    override fun log(level: LoggingLevel, message: () -> Any?) {
        noCoLogger.log(level.kloggingLevel, message())
    }

    /**
     * Emit and send a log message and an associated exception with the specified level.
     */
    override fun <E : Throwable> log(
        level: LoggingLevel,
        exception: E,
        message: (E) -> Any?,
    ) {
        noCoLogger.log(level.kloggingLevel, exception, message(exception))
    }
}
