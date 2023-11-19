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

package io.klogging.config

import io.klogging.Level.INFO
import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.sending.STDERR
import io.klogging.sending.STDOUT

/** Simple sink configuration for rendering simple strings to the standard output stream. */
public val STDOUT_SIMPLE: SinkConfiguration =
    SinkConfiguration(RENDER_SIMPLE, STDOUT)

/** Simple sink configuration for rendering simple strings to the standard error stream. */
public val STDERR_SIMPLE: SinkConfiguration =
    SinkConfiguration(RENDER_SIMPLE, STDERR)

/**
 * Simple default configuration for logging to the standard error stream.
 *
 * - All loggers are included.
 * - All events at [INFO] or higher level are included.
 */
public val DEFAULT_CONSOLE: KloggingConfiguration.() -> Unit = {
    sink("console", STDERR_SIMPLE)
    logging { fromMinLevel(INFO) { toSink("console") } }
}

/** Simple sink configuration for rendering ANSI-coloured strings to the standard output stream. */
public val STDOUT_ANSI: SinkConfiguration =
    SinkConfiguration(RENDER_ANSI, STDOUT)

/** Simple sink configuration for rendering ANSI-coloured strings to the standard error stream. */
public val STDERR_ANSI: SinkConfiguration =
    SinkConfiguration(RENDER_ANSI, STDERR)

/**
 * Simple default configuration for logging ANSI-coloured strings to the standard error stream.
 *
 * - All loggers are included.
 * - All events at [INFO] or higher level are included.
 */
public val ANSI_CONSOLE: KloggingConfiguration.() -> Unit = {
    sink("console", STDERR_ANSI)
    logging { fromMinLevel(INFO) { toSink("console") } }
}
