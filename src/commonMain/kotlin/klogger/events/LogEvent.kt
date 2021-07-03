package klogger.events

import kotlinx.serialization.Serializable

expect fun newId(): String

@Serializable
data class LogEvent(
    val id: String,
    val timestamp: Timestamp,
    val name: String,
    val level: Level,
    val message: String,
    val items: Map<String, String>,
)

