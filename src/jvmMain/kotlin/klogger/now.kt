package klogger

import java.time.Instant

actual fun now(): Timestamp {
    val instant = Instant.now()
    return Timestamp(instant.epochSecond, instant.nano.toLong())
}