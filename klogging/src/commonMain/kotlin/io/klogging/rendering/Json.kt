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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serialize a map with string keys to JSON.
 */
public fun serializeMap(map: EventItems, omitNullValues: Boolean = true): String {
    val element = map.toJsonElement(omitNullValues)
    return element.toString()
}

private fun primitive(value: Any?): JsonPrimitive = when (value) {
    null -> JsonNull
    is Number -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    else -> JsonPrimitive(value.toString())
}

private fun List<*>.toJsonElement(omitNullValues: Boolean): JsonElement {
    val list: MutableList<JsonElement> = mutableListOf()
    this.forEach {
        val value = it ?: if (omitNullValues) return@forEach else null
        when (value) {
            is Map<*, *> -> list.add((value).toJsonElement(omitNullValues))
            is List<*> -> list.add(value.toJsonElement(omitNullValues))
            else -> list.add(primitive(value))
        }
    }
    return JsonArray(list)
}

private fun Map<*, *>.toJsonElement(omitNullValues: Boolean): JsonElement {
    val map: MutableMap<String, JsonElement> = mutableMapOf()
    this.forEach {
        val keyString = it.key as? String ?: return@forEach
        val value = it.value ?: if (omitNullValues) return@forEach else null
        when (value) {
            is Map<*, *> -> map[keyString] = (value).toJsonElement(omitNullValues)
            is List<*> -> map[keyString] = value.toJsonElement(omitNullValues)
            else -> map[keyString] = primitive(value)
        }
    }
    return JsonObject(map)
}
