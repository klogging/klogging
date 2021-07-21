package ktlogging

import kotlinx.coroutines.delay
import ktlogging.config.LogDispatcher
import ktlogging.config.LoggingConfig
import ktlogging.config.LoggingConfiguration
import ktlogging.config.ROOT_CONFIG
import ktlogging.events.Level
import ktlogging.events.LogEvent
import ktlogging.events.Timestamp
import ktlogging.events.hostname
import java.time.Instant
import kotlin.random.Random
import kotlin.random.nextULong

fun timestampNow() = Instant.now().let { Timestamp(it.epochSecond, it.nano.toLong()) }

fun randomLoggerName() = Random.nextInt().toString(16)

fun randomString() = Random.nextULong().toString(16)

fun randomLevel() = Level.values().random()

fun logEvent(
    timestamp: Timestamp = timestampNow(),
    host: String = hostname,
    name: String = randomLoggerName(),
    level: Level = randomLevel(),
    message: String = randomString(),
    stackTrace: String? = null,
    items: Map<String, String> = mapOf(),
) = LogEvent(
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
    LoggingConfiguration
        .setConfigs(LoggingConfig(ROOT_CONFIG, Level.TRACE, listOf(LogDispatcher("Test") { e -> saved.add(e) })))
    return saved
}
