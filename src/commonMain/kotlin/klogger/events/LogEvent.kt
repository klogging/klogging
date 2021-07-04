package klogger.events

import kotlinx.serialization.Serializable

expect fun newId(): String

expect fun hostname(): String

@Serializable
data class LogEvent(
    val id: String,
    val timestamp: Timestamp,
    val host: String = hostname(),
    val name: String,
    val level: Level,
    val message: String,
    val items: Map<String, String>,
)

