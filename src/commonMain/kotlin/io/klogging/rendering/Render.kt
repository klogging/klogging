/*

   Copyright 2022 Michael Strasser.

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

/** Right-align the string into a fixed-width space. */
public fun String.rightAlign(width: Int): String =
    (" ".repeat(width) + this).let { it.substring(it.length - width) }
