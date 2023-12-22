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

import io.klogging.internal.debug
import io.klogging.internal.warn
import io.klogging.rendering.RenderString
import io.klogging.rendering.Renderer
import io.klogging.sending.SendString
import io.klogging.sending.Sender
import kotlin.reflect.full.createInstance

internal actual fun loadRendererByName(className: String?): RenderString? = className?.let { name ->
    try {
        val renderer = (Class.forName(name).kotlin.createInstance() as Renderer).renderString()
        debug("File Configuration", "Loaded Renderer class $name")
        renderer
    } catch (ex: Exception) {
        warn("File Configuration", "Failed to load Renderer class $name", ex)
        null
    }
}

internal actual fun loadSenderByName(className: String?): SendString? = className?.let { name ->
    try {
        val sender = (Class.forName(name).kotlin.createInstance() as Sender).sendString()
        debug("File Configuration", "Loaded Sender class $name")
        sender
    } catch (ex: Exception) {
        warn("File Configuration", "Failed to load Sender class $name", ex)
        null
    }
}
