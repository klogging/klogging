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

package io.klogging.render

/**
 * Simple implementation of [RenderString] for output to a console, mostly on one line.
 *
 * If there is a stack trace it is on second and following lines.
 */
public val RENDER_SIMPLE: RenderString = { e ->
    "${e.timestamp} ${e.level} [${e.context}] ${e.logger} ${e.items} : ${e.message}" +
        if (e.stackTrace != null) "\n${e.stackTrace}" else ""
}
