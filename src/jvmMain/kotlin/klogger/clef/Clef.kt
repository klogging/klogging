package klogger.clef

import klogger.events.LogEvent
import java.net.HttpURLConnection
import java.net.URL
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

actual fun sendClef(clefEvent: String, server: String) {
    val bytes = clefEvent.toByteArray()
    val url = URL("${server}/api/events/raw?clef")
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/vnd.serilog.clef")
    conn.doOutput = true
    conn.outputStream.use { it.write(bytes) }
    conn.inputStream.use { it.read() }
}
