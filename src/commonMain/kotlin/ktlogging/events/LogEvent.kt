package ktlogging.events

import kotlinx.serialization.Serializable

expect fun hostname(): String

@Serializable
data class LogEvent(
    val timestamp: Timestamp,
    val host: String = hostname(),
    val logger: String,
    val level: Level,
    val template: String? = null,
    val message: String,
    val stackTrace: String?,
    val items: Map<String, String>,
)
