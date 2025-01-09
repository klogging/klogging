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

package io.klogging.context

import io.klogging.events.EventItems
import io.klogging.internal.KloggingEngine
import kotlin.coroutines.CoroutineContext

/** Type alias for an item in a context. */
public typealias ContextItem = Pair<String, Any?>

/**
 * Functional type that returns a map of context items.
 */
public typealias ItemExtractor = () -> EventItems

/**
 * Object for configuring context information that will be included in log events.
 */
public object Context {

    /**
     * Add one or more items to the base context, to be included in every log event.
     *
     * @param contextItems context items to add to the base context
     */
    public fun addBaseContext(vararg contextItems: ContextItem) {
        KloggingEngine.baseContextItems.putAll(contextItems)
    }

    /**
     * Remove one or more items from the base context so they are no longer included
     * in every log event.
     *
     * @param keys of context items to remove from the base context
     */
    public fun removeBaseContext(vararg keys: String) {
        keys.forEach { key ->
            KloggingEngine.baseContextItems.remove(key)
        }
    }

    /**
     * Clear base context items.
     */
    public fun clearBaseContext() {
        KloggingEngine.baseContextItems.clear()
    }

    /**
     * Function to add a [ContextItemExtractor] that extracts an item map from a
     * coroutine context element.
     *
     * @param key key to a coroutine context element
     * @param extractor lambda that returns extracted context items
     */
    public fun <T : CoroutineContext.Element> addContextItemExtractor(
        key: CoroutineContext.Key<T>,
        extractor: suspend (T) -> EventItems,
    ) {
        @Suppress("UNCHECKED_CAST")
        KloggingEngine.otherContextExtractors[key] = extractor as ContextItemExtractor
    }

    /**
     * Clear any [ContextItemExtractor] functions.
     */
    public fun clearContextItemExtractors() {
        KloggingEngine.otherContextExtractors.clear()
    }

    /**
     * Add an [ItemExtractor] that will be used to add items to every log event.
     *
     * @param extractor function that extracts context items from somewhere
     */
    public fun addItemExtractor(extractor: ItemExtractor) {
        KloggingEngine.otherItemExtractors.add(extractor)
    }

    /**
     * Clear any [ItemExtractor] functions.
     */
    public fun clearItemExtractors() {
        KloggingEngine.otherItemExtractors.clear()
    }
}
