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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Container for objects to be stored in coroutine contexts and used in logging.
 *
 * @property items context items in this context
 */
@Suppress("WRONG_NEWLINES_AROUND_KDOC") // Stop Diktat false positives
public class LogContext internal constructor(
    private val items: MutableMap<String, Any?>,
) : AbstractCoroutineContextElement(LogContext) {

    /**
     * Key used to retrieve the [LogContext] from the coroutine context.
     */
    public companion object Key : CoroutineContext.Key<LogContext>

    /**
     * Retrieve an object from the [LogContext].
     * @param key key of the item to retrieve
     */
    public fun get(key: String): Any? = items[key]

    /**
     * Get all objects from the [LogContext] as a map.
     */
    public fun getAll(): EventItems = items.toMap()

    /**
     * Put zero or more items into the [LogContext].
     * @param newItems context items to add
     */
    internal fun putItems(vararg newItems: ContextItem) {
        items.putAll(newItems)
    }

    /**
     * Remove zero or more items from the [LogContext].
     * @param keys keys of items to remove
     */
    internal fun removeItem(vararg keys: String) {
        keys.forEach { items.remove(it) }
    }

    /**
     * Render the context as string with the number of items in it.
     */
    public override fun toString(): String = "LogContext [${items.size}]"
}

/**
 * Utility function for constructing a [LogContext] with specified items.
 * @param items context items to include in the context
 * @return a [LogContext] containing the specified items
 */
public suspend fun logContext(vararg items: ContextItem): CoroutineContext {
    val allItems = coroutineContext[LogContext]
        ?.getAll()?.toMutableMap()
        ?: mutableMapOf()
    allItems.putAll(items)
    return LogContext(allItems)
}

/**
 * Utility function that stores items into a [LogContext] in the current
 * coroutine scope.
 *
 * @param items context items to include in the context
 * @param block lambda that runs in the scope
 * @return the value returned by [block]
 */
public suspend fun <R> withLogContext(
    vararg items: ContextItem,
    block: suspend CoroutineScope.() -> R,
): R = withContext(logContext(*items)) {
    block()
}

/**
 * Adds zero or more items to the [LogContext] in the current coroutine scope.
 * @param items context items to add
 */
public suspend fun addToContext(vararg items: ContextItem) {
    coroutineContext[LogContext]?.putItems(*items)
}

/**
 * Removes zero or more items from the [LogContext] in the current coroutine scope.
 * @param keys keys of the items to remove
 */
public suspend fun removeFromContext(vararg keys: String) {
    coroutineContext[LogContext]?.removeItem(*keys)
}
