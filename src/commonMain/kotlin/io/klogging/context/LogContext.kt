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

package io.klogging.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Container for objects to be stored in coroutine contexts and used in logging.
 */
public class LogContext internal constructor(
    private val items: MutableMap<String, Any?>
) : AbstractCoroutineContextElement(LogContext) {

    /** Key used to retrieve the [LogContext] from the coroutine context. */
    public companion object Key : CoroutineContext.Key<LogContext>

    /** Retrieve an object from the [LogContext]. */
    public fun get(key: String): Any? = items[key]

    /** Get all objects from the [LogContext] as a map. */
    public fun getAll(): Map<String, Any?> = items.toMap()

    /** Put zero or more items into the [LogContext]. */
    internal fun putItems(vararg newItems: Pair<String, Any?>) {
        items.putAll(newItems)
    }

    /** Remove zero or more items from the [LogContext]. */
    internal fun removeItem(vararg keys: String) {
        keys.forEach { items.remove(it) }
    }

    override fun toString(): String = "LogContext [${items.size}]"
}

/**
 * Utility function for constructing a [LogContext] with specified items.
 */
public suspend fun logContext(vararg items: Pair<String, Any?>): CoroutineContext {
    val allItems = coroutineContext[LogContext]
        ?.getAll()?.toMutableMap()
        ?: mutableMapOf()
    allItems.putAll(items)
    return LogContext(allItems)
}

/**
 * Adds zero or more items to the [LogContext] in the current coroutine scope.
 */
public suspend fun addToContext(vararg items: Pair<String, Any?>) {
    coroutineContext[LogContext]?.putItems(*items)
}

/**
 * Removes zero or more items from the [LogContext] in the current coroutine scope.
 */
public suspend fun removeFromContext(vararg keys: String) {
    coroutineContext[LogContext]?.removeItem(*keys)
}
