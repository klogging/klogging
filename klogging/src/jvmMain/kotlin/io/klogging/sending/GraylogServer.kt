/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.sending

import io.klogging.internal.trace
import io.klogging.internal.warn
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Send a GELF event string to a Graylog server using UDP.
 *
 * @param endpoint [Endpoint] of the Graylog server
 * @param eventString GELF-formatted log event
 */
internal actual fun sendToGraylog(endpoint: Endpoint, eventString: String) {
    val bytes = eventString.toByteArray()
    val packet = DatagramPacket(
        bytes,
        0,
        bytes.size,
        InetAddress.getByName(endpoint.host),
        endpoint.port,
    )
    try {
        trace("Graylog", "Sending GELF event in context ${Thread.currentThread().name}")
        DatagramSocket().use { it.send(packet) }
    } catch (e: IOException) {
        warn("Graylog", "Exception sending GELF message: $e")
    }
}
