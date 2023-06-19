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
import io.klogging.rendering.RenderString

/** Functional type used for sending a string to a target somewhere. */
public typealias SendString = suspend (String) -> Unit

/** Function type for sending a batch of log events somewhere. */
public typealias EventSender = suspend (List<LogEvent>) -> Unit

/** Convert a [RenderString] and [SendString] into an [EventSender]. */
public fun senderFrom(renderer: RenderString, sender: SendString): EventSender = { batch ->
    sender(batch.map { renderer(it) }.joinToString("\n"))
}
