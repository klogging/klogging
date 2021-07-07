import klogger.events.Level
import klogger.events.Timestamp
import kotlinx.coroutines.delay
import java.time.Instant
import kotlin.random.Random
import kotlin.random.nextULong

fun timestampNow() = Instant.now().let { Timestamp(it.epochSecond, it.nano.toLong()) }

fun randomLoggerName() = Random.nextInt().toString(16)

fun randomString() = Random.nextULong().toString(16)

fun randomLevel() = Level.values().random()

suspend fun waitForDispatch(millis: Long = 50) = delay(millis)
