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

import io.klogging.events.LogEvent

/**
 * Extension function that replaces values of placeholders in the `message` field with
 * values of items whose keys match them.
 *
 * For example, an event with message "User ID {userId} signed in" and items that include
 * "userId" to "uid_nO59TmsQfqHxqbFL", [evalTemplate] returns the string
 * "User ID uid_nO59TmsQfqHxqbFL signed in".
 */
public fun LogEvent.evalTemplate(): String = items.entries
    .filter { entry -> entry.value != null }
    .fold(message) { message, (key, value) -> message.replace("{$key}", value.toString()) }