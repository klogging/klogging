package ktlogging.json

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serialize a map with string keys to JSON.
 */
public fun serializeMap(map: Map<String, Any?>): String {
    val element = map.toJsonElement()
    return element.toString()
}

private fun primitive(value: Any): JsonPrimitive = when (value) {
    is Number -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    else -> JsonPrimitive(value.toString())
}

private fun List<*>.toJsonElement(): JsonElement {
    val list: MutableList<JsonElement> = mutableListOf()
    this.forEach {
        val value = it ?: return@forEach
        when (value) {
            is Map<*, *> -> list.add((value).toJsonElement())
            is List<*> -> list.add(value.toJsonElement())
            else -> list.add(primitive(value))
        }
    }
    return JsonArray(list)
}

private fun Map<*, *>.toJsonElement(): JsonElement {
    val map: MutableMap<String, JsonElement> = mutableMapOf()
    this.forEach {
        val key = it.key as? String ?: return@forEach
        val value = it.value ?: return@forEach
        when (value) {
            is Map<*, *> -> map[key] = (value).toJsonElement()
            is List<*> -> map[key] = value.toJsonElement()
            else -> map[key] = primitive(value)
        }
    }
    return JsonObject(map)
}
