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

package io.klogging.rendering

/**
 * Implementation of [RenderString] like that for Log4J2 with ANSI colouring of
 * level output to a console.
 */
public val RENDER_ANSI: RenderString = renderAnsi(DEFAULT_MAX_WIDTH, DEFAULT_MAX_WIDTH)

/**
 * Implementation of [RenderString] like that for Log4J2 with ANSI colouring of
 * level output to a console.
 */
public fun renderAnsi(
    contextWidth: Int,
    loggerWidth: Int,
): RenderString = RenderString { event ->
    buildString {
        append("${event.timestamp.localTime} ${event.level.colour5}")
        if (contextWidth > 0 && event.context != null)
            append(" [${event.context.shortenRight(contextWidth)}]")
        if (loggerWidth > 0)
            append(" : ${event.logger.shortenRight(loggerWidth)}")
        append(" : ${event.evalTemplate()}")
        append(event.itemsAndStackTrace)
    }
}
