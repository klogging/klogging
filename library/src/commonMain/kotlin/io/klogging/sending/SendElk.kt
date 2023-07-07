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

import io.klogging.events.LogEvent
import io.klogging.rendering.RENDER_ECS
import kotlinx.serialization.Serializable

/** Model of an ELK server endpoint */
@Serializable
public data class ElkEndpoint(
    val url: String,
    val checkCertificate: String = "true"
)

/**
 * Send a batch of events to an Elk server in ECS format.
 */
internal expect fun elkSender(endpoint: ElkEndpoint): EventSender

internal fun elkBatch(batch: List<LogEvent>): String = batch
    .joinToString("\n") { RENDER_ECS(it) }
