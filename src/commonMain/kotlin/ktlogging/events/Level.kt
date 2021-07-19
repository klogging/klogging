package ktlogging.events

import kotlinx.serialization.Serializable

@Serializable
enum class Level {
    NONE, TRACE, DEBUG, INFO, WARN, ERROR, FATAL
}
