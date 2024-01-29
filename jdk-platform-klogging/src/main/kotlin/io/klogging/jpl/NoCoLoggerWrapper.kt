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

package io.klogging.jpl

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import java.text.MessageFormat
import java.util.ResourceBundle

/**
 * Extension property on JDK system log levels that converts to the corresponding
 * Klogging levels.
 */
@Suppress("CUSTOM_GETTERS_SETTERS")
internal val System.Logger.Level.toKloggingLevel: Level
    get() = when (this) {
        System.Logger.Level.TRACE -> TRACE
        System.Logger.Level.DEBUG -> DEBUG
        System.Logger.Level.INFO -> INFO
        System.Logger.Level.WARNING -> WARN
        System.Logger.Level.ERROR -> ERROR
        System.Logger.Level.ALL -> TRACE
        System.Logger.Level.OFF -> NONE
    }

/**
 * JDK Platform Logging implementation of [System.Logger] that wraps a Klogging `NoCoLogger` instance.
 *
 * @param noCoLogger the wrapped Klogging logger
 */
@Suppress("WRONG_NEWLINES") // Stop Diktat false positives
public class NoCoLoggerWrapper(private val noCoLogger: io.klogging.NoCoLogger) : System.Logger {
    /**
     * Return the wrapped logger name as the [System.Logger] name.
     */
    override fun getName(): String = noCoLogger.name

    /**
     * Determine if this logger will log at a specified JDK system logger level.
     *
     * @param level JDK system logger level
     * @return true if this logger will log at the specified level
     */
    override fun isLoggable(level: System.Logger.Level): Boolean =
        noCoLogger.isLevelEnabled(level.toKloggingLevel)

    /**
     * Log a message and possibly an associated throwable object.
     *
     * @param level the log message level
     * @param bundle a resource bundle to localise messages: CURRENTLY IGNORED
     * @param msg a string message; can be null.
     * @param thrown a [Throwable] object associated with the message; can be null
     */
    override fun log(level: System.Logger.Level, bundle: ResourceBundle?, msg: String?, thrown: Throwable?) {
        msg?.let {
            thrown?.let {
                noCoLogger.log(level.toKloggingLevel, thrown, msg)
            } ?: noCoLogger.log(level.toKloggingLevel, msg)
        } ?: thrown?.let {
            noCoLogger.log(level.toKloggingLevel, thrown.message ?: "Something went wrong", thrown)
        } ?: // Why bother?
        noCoLogger.log(level.toKloggingLevel, "Null log message and throwable")
    }

    /**
     * Log a formatted message, possibly with params.
     *
     * @param level the log message level
     * @param bundle a resource bundle to localise messages: CURRENTLY IGNORED
     * @param format a string message or message format; can be null.
     * @param params parameters to use with the message format; can be empty or null
     */
    override fun log(level: System.Logger.Level, bundle: ResourceBundle?, format: String?, params: Array<Any>?) {
        format?.let {
            params?.let {
                noCoLogger.log(level.toKloggingLevel, MessageFormat(format).format(params))
            } ?: noCoLogger.log(level.toKloggingLevel, format)
        } ?: noCoLogger.log(level.toKloggingLevel, "Null message format")
    }
}
