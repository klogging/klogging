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

package io.klogging.context

import io.klogging.events.EventItems
import kotlin.coroutines.CoroutineContext

/**
 * Functional type that returns a map of logging items.
 */
internal typealias ContextItemExtractor = suspend (CoroutineContext.Element) -> EventItems

/**
 * Get other context event items from a coroutine context.
 */
internal suspend fun <T : CoroutineContext.Element> CoroutineContext.otherContextItems(
    key: CoroutineContext.Key<T>,
    itemExtractor: suspend (T) -> EventItems
): EventItems = get(key)?.let { itemExtractor(it) } ?: mapOf()
