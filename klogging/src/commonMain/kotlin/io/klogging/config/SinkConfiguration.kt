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

/** Configuration of a logging sink, comprising a sender and a renderer. */
public class SinkConfiguration(
    internal val renderer: RenderString = RENDER_SIMPLE,
    internal val stringSender: SendString = STDOUT,
    internal val eventSender: EventSender = senderFrom(renderer, stringSender),
)

/** Sink configuration for a [Seq](https://datalust.co/seq) server. */
public fun seq(server: String, renderer: RenderString = RENDER_CLEF): SinkConfiguration =
    SinkConfiguration(renderer, seqServer(server))

/** Sink configuration for a [Graylog](https://www.graylog.org/) server. */
public fun graylog(
    host: String,
    port: Int,
    renderer: RenderString = RENDER_GELF,
): SinkConfiguration =
    SinkConfiguration(renderer, graylogServer(Endpoint(host, port)))
