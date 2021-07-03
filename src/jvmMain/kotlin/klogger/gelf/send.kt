package klogger.gelf

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

actual fun send(gelfEvent: String, endpoint: Endpoint) {
    val bytes = gelfEvent.toByteArray()
    val packet = DatagramPacket(bytes, 0, bytes.size, InetAddress.getByName(endpoint.host), endpoint.port)
    DatagramSocket().use { it.send(packet) }
}