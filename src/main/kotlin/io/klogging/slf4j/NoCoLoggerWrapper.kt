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

package io.klogging.slf4j

import io.klogging.Level
import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.NoCoLogger
import org.slf4j.helpers.MarkerIgnoringBase

class NoCoLoggerWrapper(
    private val noCoLogger: NoCoLogger
) : MarkerIgnoringBase() {

    override fun getName(): String = noCoLogger.name

    override fun isTraceEnabled(): Boolean = noCoLogger.isTraceEnabled()

    override fun trace(msg: String?) {
        noCoLogger.log(TRACE, msg)
    }

    override fun trace(format: String?, arg: Any?) {
        if (format != null) noCoLogger.log(TRACE, format, arg)
    }

    override fun trace(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) noCoLogger.log(TRACE, format, arg1, arg2)
    }

    override fun trace(format: String?, vararg arguments: Any?) {
        if (format != null) noCoLogger.log(TRACE, format, *arguments)
    }

    private fun logWithThrowable(level: Level, msg: String?, t: Throwable?) {
        val ex = t?.let { Exception(it) }
        if (msg != null)
            if (ex != null) noCoLogger.log(level, ex, msg) else noCoLogger.log(level, msg)
        else if (ex != null) noCoLogger.log(level, ex)
    }

    override fun trace(msg: String?, t: Throwable?) {
        logWithThrowable(TRACE, msg, t)
    }

    override fun isDebugEnabled(): Boolean = noCoLogger.isDebugEnabled()

    override fun debug(msg: String?) {
        noCoLogger.log(DEBUG, msg)
    }

    override fun debug(format: String?, arg: Any?) {
        if (format != null) noCoLogger.log(DEBUG, format, arg)
    }

    override fun debug(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) noCoLogger.log(DEBUG, format, arg1, arg2)
    }

    override fun debug(format: String?, vararg arguments: Any?) {
        if (format != null) noCoLogger.log(DEBUG, format, *arguments)
    }

    override fun debug(msg: String?, t: Throwable?) {
        logWithThrowable(DEBUG, msg, t)
    }

    override fun isInfoEnabled(): Boolean = noCoLogger.isInfoEnabled()

    override fun info(msg: String?) {
        noCoLogger.log(INFO, msg)
    }

    override fun info(format: String?, arg: Any?) {
        if (format != null) noCoLogger.log(INFO, format, arg)
    }

    override fun info(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) noCoLogger.log(INFO, format, arg1, arg2)
    }

    override fun info(format: String?, vararg arguments: Any?) {
        if (format != null) noCoLogger.log(INFO, format, *arguments)
    }

    override fun info(msg: String?, t: Throwable?) {
        logWithThrowable(INFO, msg, t)
    }

    override fun isWarnEnabled(): Boolean = noCoLogger.isWarnEnabled()

    override fun warn(msg: String?) {
        noCoLogger.log(WARN, msg)
    }

    override fun warn(format: String?, arg: Any?) {
        if (format != null) noCoLogger.log(WARN, format, arg)
    }

    override fun warn(format: String?, vararg arguments: Any?) {
        if (format != null) noCoLogger.log(WARN, format, *arguments)
    }

    override fun warn(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) noCoLogger.log(WARN, format, arg1, arg2)
    }

    override fun warn(msg: String?, t: Throwable?) {
        logWithThrowable(WARN, msg, t)
    }

    override fun isErrorEnabled(): Boolean = noCoLogger.isErrorEnabled()

    override fun error(msg: String?) {
        noCoLogger.log(ERROR, msg)
    }

    override fun error(format: String?, arg: Any?) {
        if (format != null) noCoLogger.log(ERROR, format, arg)
    }

    override fun error(format: String?, arg1: Any?, arg2: Any?) {
        if (format != null) noCoLogger.log(ERROR, format, arg1, arg2)
    }

    override fun error(format: String?, vararg arguments: Any?) {
        if (format != null) noCoLogger.log(ERROR, format, *arguments)
    }

    override fun error(msg: String?, t: Throwable?) {
        logWithThrowable(ERROR, msg, t)
    }
}
