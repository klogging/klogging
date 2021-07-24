package io.klogging.gelf

import io.klogging.events.LogEvent
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

private const val GELF_TEMPLATE = """{"version":"1.1","host":"%s","short_message":"%s",%s"timestamp":%s,"level":%d,%s}"""
private const val STACK_TEMPLATE = """"full_message":"%s","""

public actual fun LogEvent.toGelf(): String {

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

public actual fun dispatchGelf(gelfEvent: String, endpoint: Endpoint) {
    val bytes = gelfEvent.toByteArray()
    val packet = DatagramPacket(bytes, 0, bytes.size, InetAddress.getByName(endpoint.host), endpoint.port)
    try {
        DatagramSocket().use { it.send(packet) }
    } catch (e: IOException) {
        System.err.println("Exception sending GELF message: $e")
    }
}
