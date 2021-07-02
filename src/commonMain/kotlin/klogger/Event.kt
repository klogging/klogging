package klogger

import kotlinx.serialization.Serializable

expect fun newId(): String

@Serializable
data class Event(
    val id: String,
    val timestamp: Timestamp,
    val name: String,
    val level: Level,
    val template: String,
    val items: Map<String, String>,
)

