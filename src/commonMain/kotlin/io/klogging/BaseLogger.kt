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

import io.klogging.config.KloggingConfiguration
import io.klogging.events.Level

/**
 * Base interface of [Klogger] interface for use in coroutines and
 * [NoCoLogger] interface for use outside coroutines.
 */
public interface BaseLogger {

    /** Name of the logger: usually a class name in Java. */
    public val name: String

    /**
     * Minimum level at which to emit log events, determined from current
     * configuration.
     */
    public fun minLevel(): Level = KloggingConfiguration.minimumLevelOf(name)

    /**
     * Check whether this logger will emit log events at the specified logging
     * level.
     */
    public fun isLevelEnabled(level: Level): Boolean = minLevel() <= level
    /** Is this logger enabled to emit [Level.TRACE] events? */
    public fun isTraceEnabled(): Boolean = minLevel() <= Level.TRACE
    /** Is this logger enabled to emit [Level.DEBUG] events? */
    public fun isDebugEnabled(): Boolean = minLevel() <= Level.DEBUG
    /** Is this logger enabled to emit [Level.INFO] events? */
    public fun isInfoEnabled(): Boolean = minLevel() <= Level.INFO
    /** Is this logger enabled to emit [Level.WARN] events? */
    public fun isWarnEnabled(): Boolean = minLevel() <= Level.WARN
    /** Is this logger enabled to emit [Level.ERROR] events? */
    public fun isErrorEnabled(): Boolean = minLevel() <= Level.ERROR
    /** Is this logger enabled to emit [Level.FATAL] events? */
    public fun isFatalEnabled(): Boolean = minLevel() <= Level.FATAL
}
