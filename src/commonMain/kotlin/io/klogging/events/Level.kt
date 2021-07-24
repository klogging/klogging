package io.klogging.events

import kotlinx.serialization.Serializable

@Serializable
public enum class Level {
    NONE, TRACE, DEBUG, INFO, WARN, ERROR, FATAL
}
