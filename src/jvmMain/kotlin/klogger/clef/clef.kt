package klogger.clef

import klogger.Event
import java.time.Instant

const val CLEF_TEMPLATE = """{"@t":"%s","@m":"%s","@l":"%s",%s}"""

actual fun clef(event: Event): String {

    val itemsJson = (event.items + mapOf("logger" to event.name))
        .map { (k, v) -> """"$k":"$v"""" }
        .joinToString(",")

    return CLEF_TEMPLATE.format(
        Instant.ofEpochSecond(event.timestamp.epochSeconds, event.timestamp.nanos).toString(),
        event.message,
        event.level,
        itemsJson,
    )
}
