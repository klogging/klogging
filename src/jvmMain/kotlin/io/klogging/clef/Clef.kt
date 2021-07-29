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

package io.klogging.clef

import io.klogging.events.LogEvent
import io.klogging.json.serializeMap
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

/**
 * Serialises a [LogEvent] into [CLEF](https://docs.datalust.co/docs/posting-raw-events#compact-json-format)
 * compact JSON format.
 */
public actual fun LogEvent.toClef(): String {
    val eventMap: MutableMap<String, Any?> = (
        mapOf(
            "@t" to Instant.ofEpochSecond(timestamp.epochSeconds, timestamp.nanos).toString(),
            "@l" to level.name,
            "host" to host,
            "logger" to logger,
        ) + items
        ).toMutableMap()
    if (context != null) eventMap["context"] = context
    if (template != null) eventMap["@mt"] = template else eventMap["@m"] = message
    if (stackTrace != null) eventMap["@x"] = stackTrace

    return serializeMap(eventMap)
}

/**
 * Posts a CLEF-serialised event to the specified server using HTTP.
 *
 * Simple, initial version: send events separately.
 */
public actual fun dispatchClef(clefEvent: String, server: String) {
    val bytes = clefEvent.toByteArray()
    val url = URL("$server/api/events/raw")
    val conn = url.openConnection() as HttpURLConnection
    conn.requestMethod = "POST"
    conn.setRequestProperty("Content-Type", "application/vnd.serilog.clef")
    conn.doOutput = true
    try {
        conn.outputStream.use { it.write(bytes) }
        val response = conn.inputStream.use { String(it.readAllBytes()) }
        if (conn.responseCode >= 400) {
            System.err.println("Error response ${conn.responseCode} sending CLEF message: $response")
        }
    } catch (e: IOException) {
        System.err.println("Exception sending CLEF message: $e")
    }
}
