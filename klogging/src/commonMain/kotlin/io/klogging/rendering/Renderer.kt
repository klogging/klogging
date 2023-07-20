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

package io.klogging.rendering

import io.klogging.events.LogEvent

/** Functional type for rendering a [LogEvent] to a String. */
public typealias RenderString = (LogEvent) -> String

/**
 * Named renderer that renders a LogEvent into a string.
 *
 * @property name The name of the Renderer.
 * @property renderer The function that performs the rendering.
 */
public data class Renderer(val name: String, private val renderer: RenderString) {
    /**
     * Invokes the method with the given LogEvent as input and returns the result as a String.
     *
     * @param logEvent The LogEvent object to pass as input to the method.
     *
     * @return The result of the invocation as a String.
     */
    public operator fun invoke(logEvent: LogEvent): String = renderer(logEvent)
}

/** Right-align the string into a fixed-width space. */
public fun String.rightAlign(width: Int): String =
    (" ".repeat(width) + this).let { it.substring(it.length - width) }
