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
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Send a CLEF event string to a Seq server.
 *
 * @param url URL of the Seq server
 * @param eventString one or more CLEF-formatted, newline-separated log event(s)
 */
internal actual fun sendToSeq(
    url: String,
    apiKey: String?,
    checkCertificate: Boolean,
    eventString: String,
) {
    val conn = seqConnection(url, apiKey, checkCertificate)
    try {
        trace("Seq", "Sending events to Seq in context ${Thread.currentThread().name}")
        conn.outputStream.use { it.write(eventString.toByteArray()) }
        val response = conn.inputStream.use { String(it.readAllTheBytes()) }
        if (conn.responseCode >= 400) {
            warn("Seq", "Error response ${conn.responseCode} sending CLEF message: $response")
        }
    } catch (e: IOException) {
        warn("Seq", "Exception sending CLEF message: $e")
    }
}

/** Construct an HTTP connection to the Seq server. */
private fun seqConnection(serverUrl: String, apiKey: String?, checkCertificate: Boolean): HttpURLConnection {
    val url = URL("$serverUrl/api/events/raw")
    val conn = if (serverUrl.startsWith("https://")) {
        (url.openConnection() as HttpsURLConnection).also {
            if (!checkCertificate) Certificates.relaxHostChecking(it)
        }
    } else {
        url.openConnection() as HttpURLConnection
    }
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/vnd.serilog.clef")
    if (apiKey != null) conn.setRequestProperty("X-Seq-ApiKey", apiKey)
    conn.doOutput = true
    return conn
}
