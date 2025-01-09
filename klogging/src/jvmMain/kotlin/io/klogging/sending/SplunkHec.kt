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
import io.klogging.rendering.RenderString
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal actual fun sendToSplunk(endpoint: SplunkEndpoint, renderer: RenderString, batch: List<LogEvent>) {
    val conn = hecConnection(endpoint.hecUrl, endpoint.hecToken, endpoint.checkCertificate == "true")
    try {
        trace("Splunk", "Sending events to Splunk in context ${Thread.currentThread().name}")
        conn.outputStream.use { it.write(splunkBatch(renderer, batch).toByteArray()) }
        val response = conn.inputStream.use { String(it.readAllTheBytes()) }
        if (conn.responseCode >= 400) {
            warn("Splunk", "Error response ${conn.responseCode} sending event to Splunk: $response")
        }
    } catch (e: IOException) {
        warn("Splunk", "Exception sending message to Splunk: $e")
    }
}

private fun hecConnection(hecUrl: String, hecToken: String, checkCertificate: Boolean): HttpURLConnection {
    val conn =
        URL("$hecUrl/services/collector/event").openConnection() as HttpsURLConnection
    if (!checkCertificate) {
        Certificates.relaxHostChecking(conn)
    }
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Authorization", "Splunk $hecToken")
    conn.doOutput = true
    return conn
}

internal actual fun sendToSplunk(
    hecUrl: String,
    hecToken: String,
    checkCertificate: Boolean,
    eventString: String,
) {
    val conn = hecConnection(hecUrl, hecToken, checkCertificate)
    try {
        trace("Splunk", "Sending events to Splunk in context ${Thread.currentThread().name}")
        conn.outputStream.use { it.write(eventString.toByteArray()) }
        val response = conn.inputStream.use { String(it.readAllTheBytes()) }
        if (conn.responseCode >= 400) {
            warn("Splunk", "Error response ${conn.responseCode} sending event to Splunk: $response")
        }
    } catch (e: IOException) {
        warn("Splunk", "Exception sending message to Splunk: $e")
    }
}
