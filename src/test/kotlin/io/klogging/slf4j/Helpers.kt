package io.klogging.slf4j

import io.klogging.Level
import io.klogging.Level.TRACE
import io.klogging.config.SinkConfiguration
import io.klogging.config.loggingConfiguration
import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.random.nextULong

fun randomString() = Random.nextULong().toString(16)

suspend fun waitForDispatch(millis: Long = 50) = delay(millis)

fun savedEvents(minLevel: Level = TRACE): MutableList<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    val saveEventRenderer: RenderString = { e -> saved.add(e); "" }
    loggingConfiguration {
        sink("test", SinkConfiguration(saveEventRenderer) {})
        logging { fromMinLevel(minLevel) { toSink("test") } }
    }
    return saved
}
