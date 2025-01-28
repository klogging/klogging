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

package io.klogging.rendering

import io.klogging.events.EventItems

internal fun destructure(obj: Any): String = serializeMap(
    map = destructureToMap(obj),
    omitNullValues = false
)

internal fun destructureToMap(obj: Any): EventItems = buildMap {
    val objName = obj::class.simpleName!!
    val asString = obj.toString()
    val propStrings = asString
        .substring(objName.length + 1, asString.length - 1)
        .split(", ")
    propStrings.forEach { propString ->
        val nameValue = propString.split('=')
        put(nameValue[0], parseValue(nameValue.last()))
    }
    put("${'$'}type", objName)
}

internal fun parseValue(str: String): Any? = str.toIntOrNull()
    ?: str.toLongOrNull()
    ?: str.toDoubleOrNull()
    ?: str.toBooleanStrictOrNull()
    ?: if (str == "null") null else str
