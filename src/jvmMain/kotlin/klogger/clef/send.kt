package klogger.clef

import java.net.HttpURLConnection
import java.net.URL

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