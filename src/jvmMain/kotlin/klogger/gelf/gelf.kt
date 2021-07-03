package klogger.gelf

import klogger.LogEvent

const val GELF_HOST = "Local"
const val GELF_TEMPLATE = """{"version":"1.1","host":"%s","short_message":"%s","timestamp":%s,"level":%d,%s}"""

actual fun gelf(logEvent: LogEvent): String {

    val itemsJson = (logEvent.items + mapOf("logger" to logEvent.name))
        .map { (k, v) -> """"_$k":"$v"""" }
        .joinToString(",")

    return GELF_TEMPLATE.format(
        GELF_HOST,
        logEvent.message,
        logEvent.timestamp.toString(),
        graylogLevel(logEvent.level),
        itemsJson,
    )
}
