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

package io.klogging.config

import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RENDER_GELF
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.rendering.RenderString
import io.klogging.sending.Endpoint
import io.klogging.sending.EventSender
import io.klogging.sending.STDOUT
import io.klogging.sending.SendString
import io.klogging.sending.graylogServer
import io.klogging.sending.senderFrom
import io.klogging.sending.seqServer

/**
 * Configuration of a logging sink, comprising a sender and a renderer.
 *
 * @property renderer [RenderString] instance in this sink configuration
 * @property stringSender [SendString] instance in this sink configuration
 * @property eventSender [EventSender] instance in this sink configuration
 */
public data class SinkConfiguration(
    internal val renderer: RenderString = RENDER_SIMPLE,
    internal val stringSender: SendString = STDOUT,
    internal val eventSender: EventSender = senderFrom(renderer, stringSender),
)

/**
 * Sink configuration for a [Seq](https://datalust.co/seq) server.
 *
 * @param url URL of the Seq server
 * @param apiKey optional API key for the Seq server
 * @param checkCertificate flag to specify that the TLS server certificate is to be checked for validity
 * @param renderer [RenderString] instance to use with this sink configuration
 */
public fun seq(
    url: String,
    apiKey: String? = null,
    checkCertificate: Boolean = false,
    renderer: RenderString = RENDER_CLEF,
): SinkConfiguration =
    SinkConfiguration(renderer, seqServer(url, apiKey, checkCertificate))

/**
 * Sink configuration for a [Graylog](https://www.graylog.org/) server.
 *
 * @param host name or IP address of the server
 * @param port port of the server
 * @param renderer [RenderString] instance to use with this sink configuration
 */
public fun graylog(
    host: String,
    port: Int,
    renderer: RenderString = RENDER_GELF,
): SinkConfiguration =
    SinkConfiguration(renderer, graylogServer(Endpoint(host, port)))
