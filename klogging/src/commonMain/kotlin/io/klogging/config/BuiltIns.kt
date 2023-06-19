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

import io.klogging.rendering.RENDER_ANSI
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RENDER_GELF
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.rendering.RenderString
import io.klogging.sending.STDERR
import io.klogging.sending.STDOUT
import io.klogging.sending.SendString

/** Map of built-in configurations by name. */
internal val BUILT_IN_CONFIGURATIONS: Map<String, KloggingConfiguration.() -> Unit> by lazy {
    mapOf(
        "DEFAULT_CONSOLE" to DEFAULT_CONSOLE,
        "ANSI_CONSOLE" to ANSI_CONSOLE
    )
}

/** Map of built-in renderers by name. */
internal val BUILT_IN_RENDERERS: Map<String, RenderString> by lazy {
    mapOf(
        "RENDER_SIMPLE" to RENDER_SIMPLE,
        "RENDER_ANSI" to RENDER_ANSI,
        "RENDER_CLEF" to RENDER_CLEF,
        "RENDER_GELF" to RENDER_GELF
    )
}

/** Map of built-in senders by name. */
internal val BUILT_IN_SENDERS: Map<String, SendString> by lazy {
    mapOf(
        "STDOUT" to STDOUT,
        "STDERR" to STDERR
    )
}
