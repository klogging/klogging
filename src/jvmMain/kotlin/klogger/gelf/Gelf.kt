package klogger.gelf

import klogger.events.LogEvent
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

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

actual fun sendGelf(gelfEvent: String, endpoint: Endpoint) {
    val bytes = gelfEvent.toByteArray()
    val packet = DatagramPacket(bytes, 0, bytes.size, InetAddress.getByName(endpoint.host), endpoint.port)
    DatagramSocket().use { it.send(packet) }
}
