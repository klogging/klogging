package ktlogging.clef

import ktlogging.events.LogEvent
import ktlogging.json.serializeMap
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

/**
 * Serialises a [LogEvent] into [CLEF](https://docs.datalust.co/docs/posting-raw-events#compact-json-format)
 * compact JSON format.
 */
actual fun LogEvent.toClef(): String {
    val eventMap: MutableMap<String, Any?> = (
        mapOf(
            "@t" to Instant.ofEpochSecond(timestamp.epochSeconds, timestamp.nanos).toString(),
            "@l" to level.name,
            "host" to host,
            "logger" to logger,
        ) + items
        ).toMutableMap()
    if (template != null) eventMap["@mt"] = template
    else eventMap["@m"] = message
    if (stackTrace != null) eventMap["@x"] = stackTrace

    return serializeMap(eventMap)
}

/**
 * Posts a CLEF-serialised event to the specified server using HTTP.
 *
 * Simple, initial version: send events separately.
 */
actual fun dispatchClef(clefEvent: String, server: String) {
    val bytes = clefEvent.toByteArray()
    val url = URL("$server/api/events/raw")
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
