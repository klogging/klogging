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

import io.klogging.events.LogEvent
import io.klogging.internal.trace
import io.klogging.internal.warn
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal actual fun sendToElk(endpoint: ElkEndpoint, batch: List<LogEvent>) {
    val conn = elkConnection(endpoint)
    try {
        trace("ELK", "Sending events to ELK in context ${Thread.currentThread().name}")
        conn.outputStream.use { it.write(elkBatch(batch).toByteArray()) }
        val response = conn.inputStream.use { String(it.readAllTheBytes()) }
        if (conn.responseCode != HttpURLConnection.HTTP_OK) {
            warn("ELK", "Error response ${conn.responseCode} sending event to ELK: $response")
        }
    } catch (e: IOException) {
        warn("ELK", "exception sending message to ELK: $e")
    }
}

private fun elkConnection(endpoint: ElkEndpoint): HttpURLConnection {
    val conn = URL(endpoint.url).openConnection() as HttpsURLConnection
    if (!endpoint.checkCertificate) {
        Certificates.relaxHostChecking(conn)
    }
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/json")
    conn.doOutput = true
    return conn
}
