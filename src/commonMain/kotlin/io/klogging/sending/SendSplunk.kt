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

import io.klogging.config.evalEnv
import io.klogging.events.LogEvent
import io.klogging.events.decimalSeconds
import io.klogging.rendering.evalTemplate
import io.klogging.rendering.serializeMap
import kotlinx.serialization.Serializable

/** Model of a Splunk server HEC endpoint. */
@Serializable
public data class SplunkEndpoint(
    val hecUrl: String,
    val hecToken: String,
    val index: String = "main",
    val sourceType: String = "klogging",
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
        evalEnv(hecUrl),
        evalEnv(hecToken),
        evalEnv(index),
        evalEnv(sourceType),
        evalEnv(checkCertificate),
    )
}

/**
 * Send an event to a Splunk server using [HTTP event collector
 * (HEC)](https://docs.splunk.com/Documentation/Splunk/8.2.2/Data/HECExamples).
 */
public expect fun splunkHec(endpoint: SplunkEndpoint): EventSender

internal fun splunkBatch(endpoint: SplunkEndpoint, batch: List<LogEvent>): String = batch
    .map { splunkEvent(endpoint, it) }
    .joinToString("\n")

private const val TIME_MARKER = "XXX--TIME-MARKER--XXX"

/**
 * Convert a [LogEvent] to a JSON-formatted string for Splunk
 */
internal fun splunkEvent(endpoint: SplunkEndpoint, event: LogEvent): String {
    val eventMap: Map<String, Any?> = (
        mapOf(
            "logger" to event.logger,
            "level" to event.level.name,
            "context" to event.context,
            "stackTrace" to event.stackTrace,
            "message" to event.evalTemplate()
        ) + event.items
        ).filterValues { it != null }
    val splunkMap: MutableMap<String, Any?> = mutableMapOf(
        "time" to TIME_MARKER, // Replace later
        "index" to endpoint.index,
        "sourcetype" to endpoint.sourceType,
        "host" to event.host,
        "event" to eventMap,
    )
    return serializeMap(splunkMap)
        .replace(""""$TIME_MARKER"""", event.timestamp.decimalSeconds)
}
