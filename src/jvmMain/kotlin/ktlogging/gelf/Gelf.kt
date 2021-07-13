package ktlogging.gelf

import ktlogging.events.LogEvent
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

const val GELF_TEMPLATE = """{"version":"1.1","host":"%s","short_message":"%s",%s"timestamp":%s,"level":%d,%s}"""
const val STACK_TEMPLATE = """"full_message":"%s","""

actual fun LogEvent.toGelf(): String {

    val exception = stackTrace?.let { STACK_TEMPLATE.format(it) } ?: ""
    val itemsJson = (items + mapOf("logger" to logger))
        .map { (k, v) -> """"_$k":"$v"""" }
        .joinToString(",")

    return GELF_TEMPLATE.format(
        host,
        message,
        exception,
        timestamp.graylogFormat(),
        graylogLevel(level),
        itemsJson,
    )
}

actual fun dispatchGelf(gelfEvent: String, endpoint: Endpoint) {
    val bytes = gelfEvent.toByteArray()
    val packet = DatagramPacket(bytes, 0, bytes.size, InetAddress.getByName(endpoint.host), endpoint.port)
    try {
        DatagramSocket().use { it.send(packet) }
    } catch (e: IOException) {
        System.err.println("Exception sending GELF message: $e")
    }
}
