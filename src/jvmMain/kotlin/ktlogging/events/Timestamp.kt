package ktlogging.events

import java.time.Instant

public actual fun now(): Timestamp {
    val instant = Instant.now()
    return Timestamp(instant.epochSecond, instant.nano.toLong())
}

public actual fun iso(timestamp: Timestamp): String =
    Instant.ofEpochSecond(timestamp.epochSeconds, timestamp.nanos)
        .toString()
