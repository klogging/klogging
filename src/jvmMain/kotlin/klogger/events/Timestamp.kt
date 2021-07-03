package klogger.events

import klogger.events.Timestamp
import java.time.Instant

actual fun now(): Timestamp {
    val instant = Instant.now()
    return Timestamp(instant.epochSecond, instant.nano.toLong())
}

actual fun iso(timestamp: Timestamp) =
    Instant.ofEpochSecond(timestamp.epochSeconds, timestamp.nanos)
        .toString()