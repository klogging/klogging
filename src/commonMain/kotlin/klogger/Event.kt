package klogger

data class Event(
    val timestamp: Timestamp,
    val name: String,
    val level: Level,
    val template: String,
    val marker: Marker?,
    val objects: Map<String, Any>,
)

