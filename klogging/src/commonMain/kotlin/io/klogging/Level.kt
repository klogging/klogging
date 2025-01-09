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

package io.klogging

import io.klogging.Level.DEBUG
import io.klogging.Level.ERROR
import io.klogging.Level.FATAL
import io.klogging.Level.INFO
import io.klogging.Level.NONE
import io.klogging.Level.TRACE
import io.klogging.Level.WARN
import kotlinx.serialization.Serializable

/**
 * Levels of logging severity: higher ordinal values means greater severity.
 *
 * These levels are based on common JVM practice, such as
 * [Log4j](https://github.com/apache/logging-log4j2/blob/master/log4j-api/src/main/java/org/apache/logging/log4j/Level.java)
 * and
 * [SLF4J](https://github.com/qos-ch/slf4j/blob/master/slf4j-api/src/main/java/org/slf4j/event/Level.java).
 * Contrast with [syslog(2)](https://linux.die.net/man/2/syslog) levels used by such as
 * UNIX/Linux and Graylog.
 */
@Serializable
public enum class Level {
    TRACE, DEBUG, INFO, WARN, ERROR, FATAL, NONE
}

/**
 * Map [Level]s to syslog levels.  Used by other tooling such as Graylog.  The level values for
 * syslog are:
 * - 0 = Emergency (unused)
 * - 1 = Alert (unused)
 * - 2 = Critical (mapped to from [FATAL])
 * - 3 = Error (mapped to from [ERROR])
 * - 4 = Warning (mapped to from [WARN])
 * - 5 = Notice (unused)
 * - 6 = Informational (mapped to from [INFO])
 * - 7 = Debug (mapped to from [NONE], [TRACE], and [DEBUG])
 *
 * See [syslog(2)](https://linux.die.net/man/2/syslog)
 *
 * @todo [NONE] should not map to syslog's Debug?  Perhaps return an `Int?`?
 */
public val Level.syslog: Int
    get() = when (this) {
        NONE -> 7
        TRACE -> 7
        DEBUG -> 7
        INFO -> 6
        WARN -> 4
        ERROR -> 3
        FATAL -> 2
    }
