package klogger.clef

import klogger.LogEvent
import java.time.Instant

const val CLEF_TEMPLATE = """{"@t":"%s","@m":"%s","@l":"%s",%s}"""

actual fun clef(logEvent: LogEvent): String {

    val itemsJson = (logEvent.items + mapOf("logger" to logEvent.name))
        .map { (k, v) -> """"$k":"$v"""" }
        .joinToString(",")

    return CLEF_TEMPLATE.format(
        Instant.ofEpochSecond(logEvent.timestamp.epochSeconds, logEvent.timestamp.nanos).toString(),
        logEvent.message,
        logEvent.level,
        itemsJson,
    )
}
