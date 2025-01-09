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
import com.hexagonkt.core.logging.LoggingPort
import io.klogging.Level
import io.klogging.impl.NoCoLoggerImpl
import io.klogging.noCoLogger

/**
 * Extension property that converts a Hexagon LoggingLevel to a corresponding Klogging Level.
 */
@Suppress("CUSTOM_GETTERS_SETTERS")
internal val LoggingLevel.kloggingLevel: Level
    get() = when (this) {
        LoggingLevel.TRACE -> Level.TRACE
        LoggingLevel.DEBUG -> Level.DEBUG
        LoggingLevel.INFO -> Level.INFO
        LoggingLevel.WARN -> Level.WARN
        LoggingLevel.ERROR -> Level.ERROR
        else -> Level.NONE
    }

/**
 * Klogging implementation of Hexagon [LoggingPort] to set up Klogging in Hexagon applications.
 */
public class KloggingAdapter : LoggingPort {
    private val loggerLevels: MutableMap<String, LoggingLevel> = mutableMapOf()

    /**
     * Return a [NoCoLoggerAdapter] that wraps a new [NoCoLoggerImpl] for logging, using the supplied name.
     *
     * @param name of the logger
     * @return a [NoCoLoggerAdapter] with a [NoCoLoggerImpl] instance for the supplied name
     */
    public override fun createLogger(name: String): LoggerPort = NoCoLoggerAdapter(NoCoLoggerImpl(name))

    /**
     * Evaluates whether the named logger is enabled to log at the specified level.
     *
     * Results are cached in a map keyed by logger name.
     */
    public override fun isLoggerLevelEnabled(name: String, level: LoggingLevel): Boolean =
        loggerLevels[name]?.let { loggerLevel ->
            loggerLevel <= level
        } ?: noCoLogger(name).isLevelEnabled(level.kloggingLevel)

    /**
     * Explicitly set the level for a named logger.
     */
    public override fun setLoggerLevel(name: String, level: LoggingLevel) {
        loggerLevels[name] = level
    }
}
