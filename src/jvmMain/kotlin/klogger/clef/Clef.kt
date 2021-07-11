package klogger.clef

import klogger.events.LogEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

actual fun LogEvent.toClef(): String {
    val eventMap: MutableMap<String, String> = (mapOf(
        "@t" to Instant.ofEpochSecond(timestamp.epochSeconds, timestamp.nanos).toString(),
        "@m" to message,
        "@l" to level.name,
        "host" to host,
        "logger" to logger,
    ) + items).toMutableMap()
    if (stackTrace != null)
        eventMap["@x"] = stackTrace

    return Json.encodeToString(eventMap)
}

actual fun dispatchClef(clefEvent: String, server: String) {
    val bytes = clefEvent.toByteArray()
    val url = URL("${server}/api/events/raw")
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/vnd.serilog.clef")
    conn.doOutput = true
    try {
        conn.outputStream.use { it.write(bytes) }
        val response = conn.inputStream.use { String(it.readAllBytes()) }
        if (conn.responseCode >= 400) {
            System.err.println("Error response ${conn.responseCode} sending CLEF message: $response")
        }
    } catch (e: IOException) {
        System.err.println("Exception sending CLEF message: $e")
    }
}
