package ktlogging.events

import kotlinx.serialization.Serializable

@Serializable
enum class Level {
    TRACE, DEBUG, INFO, WARN, ERROR, FATAL
}
