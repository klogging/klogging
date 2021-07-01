package klogger.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class LogContext internal constructor(
    private val items: MutableMap<String, Any>
) : AbstractCoroutineContextElement(LogContext) {

    companion object Key : CoroutineContext.Key<LogContext>

    fun get(key: String) = items[key]

    fun getAll() = items.toMutableMap()

    fun putAll(newItems: Array<out Pair<String, Any>>) {
        items.putAll(newItems)
    }

    fun remove(keys: Array<out String>) {
        keys.forEach { items.remove(it) }
    }

    override fun toString() = "LogContext [${items.size}]"
}

suspend fun logContext(vararg items: Pair<String, Any>): CoroutineContext {
    val allItems = coroutineContext[LogContext]?.getAll() ?: mutableMapOf()
    allItems.putAll(items)
    return LogContext(allItems)
}

suspend fun addToContext(vararg items: Pair<String, Any>) {
    coroutineContext[LogContext]?.putAll(items)
}

suspend fun removeFromContext(vararg keys: String) {
    coroutineContext[LogContext]?.remove(keys)
}
