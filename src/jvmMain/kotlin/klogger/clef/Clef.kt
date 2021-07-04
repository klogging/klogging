package klogger.clef

import klogger.events.LogEvent
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

const val CLEF_TEMPLATE = """{"@t":"%s","@m":"%s","@l":"%s","host":"%s",%s}"""

actual fun LogEvent.toClef(): String {

    val itemsJson = (items + mapOf("logger" to name))
        .map { (k, v) -> """"$k":"$v"""" }
        .joinToString(",")

    return CLEF_TEMPLATE.format(
        Instant.ofEpochSecond(timestamp.epochSeconds, timestamp.nanos).toString(),
        message,
        level,
        host,
        itemsJson,
    )
}

actual fun dispatchClef(clefEvent: String, server: String) {
    val bytes = clefEvent.toByteArray()
    val url = URL("${server}/api/events/raw?clef")
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/vnd.serilog.clef")
    conn.doOutput = true
    conn.outputStream.use { it.write(bytes) }
    conn.inputStream.use { it.read() }
}
