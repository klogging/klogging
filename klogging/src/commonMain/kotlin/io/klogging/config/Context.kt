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

import io.klogging.context.ContextItemExtractor
import io.klogging.events.EventItems
import io.klogging.internal.KloggingEngine
import kotlin.coroutines.CoroutineContext

/**
 * Object for configuring context information that will be included in log events.
 */
public object Context {

    /**
     * Add one or more items to the base context, to be included in every log event.
     */
    public fun addBaseContext(vararg contextItems: Pair<String, Any?>) {
        KloggingEngine.baseContextItems.putAll(contextItems)
    }

    /**
     * Remove one or more items from the base context so they are no longer included
     * in every log event.
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
     * @param extractor lambda
     */
    public fun <T : CoroutineContext.Element> addContextItemExtractor(
        key: CoroutineContext.Key<T>,
        extractor: suspend (T) -> EventItems,
    ) {
        @Suppress("UNCHECKED_CAST")
        KloggingEngine.otherContextExtractors[key] = extractor as ContextItemExtractor
    }
}
