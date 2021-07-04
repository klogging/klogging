package klogger.gelf

import klogger.events.LogEvent
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

val thisHost = InetAddress.getLocalHost().hostName
const val GELF_TEMPLATE = """{"version":"1.1","host":"%s","short_message":"%s","timestamp":%s,"level":%d,%s}"""

actual fun LogEvent.toGelf(): String {

    val itemsJson = (items + mapOf("logger" to name))
        .map { (k, v) -> """"_$k":"$v"""" }
        .joinToString(",")

    return GELF_TEMPLATE.format(
        thisHost,
        message,
        timestamp.graylogFormat(),
        graylogLevel(level),
        itemsJson,
    )
}

actual fun dispatchGelf(gelfEvent: String, endpoint: Endpoint) {
    val bytes = gelfEvent.toByteArray()
    val packet = DatagramPacket(bytes, 0, bytes.size, InetAddress.getByName(endpoint.host), endpoint.port)
    DatagramSocket().use { it.send(packet) }
}
