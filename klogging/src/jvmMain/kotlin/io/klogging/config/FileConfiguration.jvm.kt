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

package io.klogging.config

import io.klogging.internal.debug
import io.klogging.internal.warn
import kotlin.reflect.full.createInstance

@Suppress("UNCHECKED_CAST")
internal actual fun <T : Any> loadByClassName(className: String?): T? = className?.let { name ->
    try {
        val eventSender = Class.forName(name).kotlin.createInstance() as T
        debug("File Configuration", "Loaded EventSender class $name")
        eventSender
    } catch (ex: Exception) {
        warn("File Configuration", "Failed to load EventSender class $name", ex)
        null
    }
}
