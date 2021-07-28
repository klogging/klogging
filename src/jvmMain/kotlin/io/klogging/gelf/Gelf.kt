/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

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
