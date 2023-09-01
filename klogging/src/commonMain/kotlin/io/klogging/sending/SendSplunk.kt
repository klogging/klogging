/*

   Copyright 2021-2023 Michael Strasser.

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

import io.klogging.config.evalEnv
import io.klogging.events.LogEvent
import io.klogging.rendering.renderHec
import kotlinx.serialization.Serializable

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
}

/**
 * Send a batch of events to a Splunk server using
 * [HTTP event collector (HEC)](https://docs.splunk.com/Documentation/Splunk/8.2.2/Data/HECExamples).
 */
internal fun splunkHec(endpoint: SplunkEndpoint): EventSender = { batch ->
    SendingLauncher.launch {
        sendToSplunk(endpoint, batch)
    }
}

internal expect fun sendToSplunk(endpoint: SplunkEndpoint, batch: List<LogEvent>)

internal fun splunkBatch(endpoint: SplunkEndpoint, batch: List<LogEvent>): String =
    batch.joinToString("\n") { event ->
        renderHec(endpoint.index, endpoint.sourceType, endpoint.source)(event)
    }
