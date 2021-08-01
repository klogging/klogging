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

public interface BaseLogger {

    public val name: String

    public fun minLevel(): Level = KloggingConfiguration.minimumLevelOf(name)

    public fun isLevelEnabled(level: Level): Boolean = minLevel() <= level
    public fun isTraceEnabled(): Boolean = minLevel() <= Level.TRACE
    public fun isDebugEnabled(): Boolean = minLevel() <= Level.DEBUG
    public fun isInfoEnabled(): Boolean = minLevel() <= Level.INFO
    public fun isWarnEnabled(): Boolean = minLevel() <= Level.WARN
    public fun isErrorEnabled(): Boolean = minLevel() <= Level.ERROR
    public fun isFatalEnabled(): Boolean = minLevel() <= Level.FATAL
}
