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

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import io.klogging.internal.KloggingState

/**
 * Base interface of [Klogger] interface for use in coroutines, and
 * [NoCoLogger] interface when not using coroutines.
 */
public interface BaseLogger {

    /** Name of the logger: usually a class name in Java. */
    public val name: String

    /**
     * Minimum level at which to emit log events, determined from current
     * configuration.
     */
    public fun minLevel(): Level = KloggingState.minimumLevelOf(name)

    /**
     * Check whether this logger will emit log events at the specified logging
     * level.
     */
    public fun isLevelEnabled(level: Level): Boolean = minLevel() <= level

    /** Is this logger enabled to emit [TRACE] events? */
    public fun isTraceEnabled(): Boolean = isLevelEnabled(TRACE)

    /** Is this logger enabled to emit [DEBUG] events? */
    public fun isDebugEnabled(): Boolean = isLevelEnabled(DEBUG)

    /** Is this logger enabled to emit [INFO] events? */
    public fun isInfoEnabled(): Boolean = isLevelEnabled(INFO)

    /** Is this logger enabled to emit [WARN] events? */
    public fun isWarnEnabled(): Boolean = isLevelEnabled(WARN)

    /** Is this logger enabled to emit [ERROR] events? */
    public fun isErrorEnabled(): Boolean = isLevelEnabled(ERROR)

    /** Is this logger enabled to emit [FATAL] events? */
    public fun isFatalEnabled(): Boolean = isLevelEnabled(FATAL)
}
