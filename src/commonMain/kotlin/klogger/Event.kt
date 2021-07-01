package klogger

expect fun newId(): String

data class Event(
    val id: String,
    val timestamp: Timestamp,
    val name: String,
    val level: Level,
    val template: String,
    val marker: Marker?,
    val objects: Map<String, Any>,
)

