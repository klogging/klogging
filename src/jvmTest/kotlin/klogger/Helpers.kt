package klogger

import klogger.events.Level
import klogger.events.LogEvent
import klogger.events.Timestamp
import klogger.events.hostname
import klogger.events.newId
import kotlinx.coroutines.delay
import java.time.Instant
import kotlin.random.Random
import kotlin.random.nextULong

fun timestampNow() = Instant.now().let { Timestamp(it.epochSecond, it.nano.toLong()) }

fun randomLoggerName() = Random.nextInt().toString(16)

fun randomString() = Random.nextULong().toString(16)

fun randomLevel() = Level.values().random()

fun logEvent(
    id: String = newId(),
    timestamp: Timestamp = timestampNow(),
    host: String = hostname(),
    name: String = randomLoggerName(),
    level: Level = randomLevel(),
    message: String = randomString(),
    stackTrace: String = randomString(),
    items: Map<String, String> = mapOf(),
) = LogEvent(
    id = id,
    timestamp = timestamp,
    host = host,
    logger = name,
    level = level,
    message = message,
    stackTrace = stackTrace,
    items = items,
)

suspend fun waitForDispatch(millis: Long = 50) = delay(millis)

fun savedEvents(): MutableList<LogEvent> {
    val saved = mutableListOf<LogEvent>()
    Dispatcher.setDispatchers({ e -> saved.add(e) })
    return saved
}
