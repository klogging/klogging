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

import io.klogging.config.evalEnv
import io.klogging.events.LogEvent
import io.klogging.rendering.RenderString
import kotlinx.serialization.Serializable

/**
 * Creates a [SendString] function that sends an event string to a Splunk server using
 * the HTTP Event Collector (HEC) API.
 *
 * @param hecUrl The URL of the Splunk HEC endpoint.
 * @param hecToken The token used for authentication with the Splunk HEC.
 * @param checkCertificate Specifies whether to check the SSL certificate when making the HTTP request. Defaults to true.
 * @return A function that takes a string event and asynchronously sends it to the Splunk server.
 */
public fun splunkServer(
    hecUrl: String,
    hecToken: String,
    checkCertificate: Boolean = true,
): SendString = SendString { eventString ->
    SendingLauncher.launch {
        sendToSplunk(hecUrl, hecToken, checkCertificate, eventString)
    }
}

/** Model of a Splunk server HEC endpoint. */
@Serializable
public data class SplunkEndpoint(
    val hecUrl: String,
    val hecToken: String,
    val index: String? = null,
    val sourceType: String? = null,
    val source: String? = null,
    val checkCertificate: String = "true",
) {
    /**
     * Evaluate any environment variables into properties of the endpoint configuration.
     *
     * For example, if env var `SPLUNK_HEC_TOKEN` is set to a value and if the value of
     * `hecToken` contains `"${SPLUNK_HEC_TOKEN}"` then the value of the env var is
     * substituted into `hecToken`.
     */
    public fun evalEnv(): SplunkEndpoint = SplunkEndpoint(
        hecUrl = evalEnv(hecUrl),
        hecToken = evalEnv(hecToken),
        index = index?.let { evalEnv(it) },
        sourceType = sourceType?.let { evalEnv(it) },
        source = source?.let { evalEnv(it) },
        checkCertificate = evalEnv(checkCertificate),
    )

    public override fun toString(): String {
        val props = buildList {
            add("hecUrl=$hecUrl")
            add("hecToken=********")
            if (index != null) {
                add("index=$index")
            }
            if (sourceType != null) {
                add("sourceType=$sourceType")
            }
            if (source != null) {
                add("source=$source")
            }
            add("checkCertificate=$checkCertificate")
        }
        return "SplunkEndpoint(${props.joinToString(", ")})"
    }
}

/**
 * Send a batch of events to a Splunk server using
 * [HTTP event collector (HEC)](https://docs.splunk.com/Documentation/Splunk/8.2.2/Data/HECExamples).
 */
public class SplunkHec(
    private val endpoint: SplunkEndpoint,
    private val renderer: RenderString
) : EventSender {
    override fun invoke(batch: List<LogEvent>) {
        SendingLauncher.launch {
            sendToSplunk(endpoint, renderer, batch)
        }
    }
}

internal expect fun sendToSplunk(endpoint: SplunkEndpoint, renderer: RenderString, batch: List<LogEvent>)

internal fun splunkBatch(renderer: RenderString, batch: List<LogEvent>): String =
    batch.joinToString("\n") { event ->
        renderer(event)
    }

internal expect fun sendToSplunk(
    hecUrl: String,
    hecToken: String,
    checkCertificate: Boolean,
    eventString: String,
)
