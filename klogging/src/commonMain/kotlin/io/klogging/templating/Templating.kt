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

package io.klogging.templating

import io.klogging.events.EventItems

/**
 * Extract a map of items from a template and supplied values for the holes.
 *
 * For example, given the call `templateItems("User {userId} signed in", userId)`
 * it returns `mapOf("userId" to userId)`.
 *
 * @param template a template. See [Message templates](https://messagetemplates.org/) for
 *                 more information.
 * @param values values to fill the ‘holes’ in a template. There should be one value for
 *               each hole.
 * @return a map of the values, keyed by the text in each of the holes.
 */
public fun templateItems(template: String, vararg values: Any?): EventItems {
    val itemNames = extractItemNames(template)
    return itemNames.zip(values).toMap()
}

private enum class TextOrHole { TEXT, HOLE }

/**
 * Extract the list of holes enclosed in braces from a template.
 *
 * For example, from the template "User {id} signed in at {time}" it extracts
 * a list containing the strings "id" and "time".
 *
 * Braces are escaped by doubling them, for example, "Evaluating {{id}}" contains
 * no holes.
 */
internal fun extractItemNames(template: String): List<String> {
    val itemNames = mutableListOf<String>()
    var state = TextOrHole.TEXT
    var holeStart = 0
    template.forEachIndexed { i, c ->
        when (c) {
            '{' -> if (state == TextOrHole.TEXT) {
                holeStart = i
                state = TextOrHole.HOLE
            } else {
                if (holeStart == i - 1) {
                    state = TextOrHole.TEXT
                }
            }

            '}' -> if (state == TextOrHole.HOLE) {
                if (i - holeStart > 1) itemNames.add(template.substring(holeStart + 1, i))
                state = TextOrHole.TEXT
            }
        }
    }
    return itemNames
}
