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

package io.klogging.sending

import io.klogging.internal.trace
import io.klogging.internal.warn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Send CLEF event strings to a Seq server on the JVM.
 *
 * @param server URL of the Seq server
 *
 * @return a [SendString] suspend function that sends each event string in a separate
 *         coroutine using the IO coroutine dispatcher.
 */
public actual fun seqServer(server: String): SendString = { eventString ->
    coroutineScope {
        launch(Dispatchers.IO) {
            sendToSeq(server, eventString)
        }
    }
}

/**
 * Send a CLEF event string to a Seq server.
 *
 * @param serverUrl URL of the Seq server
 * @param eventString one or more CLEF-formatted, newline-separated log event(s)
 */
private fun sendToSeq(serverUrl: String, eventString: String) {
    val conn = seqConnection(serverUrl)
    try {
        trace("Seq", "Sending events to Seq in context ${Thread.currentThread().name}")
        conn.outputStream.use { it.write(eventString.toByteArray()) }
        val response = conn.inputStream.use { String(it.readAllBytes()) }
        if (conn.responseCode >= 400)
            warn("Seq", "Error response ${conn.responseCode} sending CLEF message: $response")
    } catch (e: IOException) {
        warn("Seq", "Exception sending CLEF message: $e")
    }
}

/** Construct an HTTP connection to the Seq server. */
private fun seqConnection(serverUrl: String): HttpURLConnection {
    val conn = URL("$serverUrl/api/events/raw").openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/vnd.serilog.clef")
    conn.doOutput = true
    return conn
}
