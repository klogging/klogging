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

package io.klogging.hexagonkt

import com.hexagonkt.core.logging.LoggerPort
import com.hexagonkt.core.logging.LoggingLevel
import com.hexagonkt.core.logging.LoggingPort
import io.klogging.Level
import io.klogging.impl.NoCoLoggerImpl
import io.klogging.noCoLogger

public class KloggingAdapter : LoggingPort {

    private val loggerLevels = mutableMapOf<String, LoggingLevel>()

    override fun createLogger(name: String): LoggerPort = NoCoLoggerAdapter(NoCoLoggerImpl(name))

    override fun isLoggerLevelEnabled(name: String, level: LoggingLevel): Boolean =
        loggerLevels[name]?.let { loggerLevel ->
            loggerLevel <= level
        } ?: noCoLogger(name).isLevelEnabled(level.kloggingLevel)

    override fun setLoggerLevel(name: String, level: LoggingLevel) {
        loggerLevels[name] = level
    }
}

/**
 * Extension property that converts a Hexagon LoggingLevel to a corresponding Klogging Level.
 */
internal val LoggingLevel.kloggingLevel: Level
    get() = when (this) {
        LoggingLevel.TRACE -> Level.TRACE
        LoggingLevel.DEBUG -> Level.DEBUG
        LoggingLevel.INFO -> Level.INFO
        LoggingLevel.WARN -> Level.WARN
        LoggingLevel.ERROR -> Level.ERROR
        else -> Level.NONE
    }
