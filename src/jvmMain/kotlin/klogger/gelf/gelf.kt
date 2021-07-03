package klogger.gelf

import klogger.Event

const val GELF_HOST = "Local"
const val GELF_TEMPLATE = """{"version":"1.1","host":"%s","short_message":"%s","timestamp":%s,"level":%d,%s}"""

actual fun gelf(event: Event): String {

    val itemsJson = (event.items + mapOf("logger" to event.name))
        .map { (k, v) -> """"_$k":"$v"""" }
        .joinToString(",")

    return GELF_TEMPLATE.format(
        GELF_HOST,
        event.template,
        event.timestamp.toString(),
        graylogLevel(event.level),
        itemsJson,
    )
}
