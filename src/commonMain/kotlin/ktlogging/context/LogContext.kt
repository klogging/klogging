package ktlogging.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class LogContext internal constructor(
    private val items: MutableMap<String, String>
) : AbstractCoroutineContextElement(LogContext) {

    companion object Key : CoroutineContext.Key<LogContext>

    fun get(key: String) = items[key]

    fun getAll() = items.toMap()

    internal fun putItems(vararg newItems: Pair<String, String>) {
        items.putAll(newItems)
    }

    internal fun removeItem(vararg keys: String) {
        keys.forEach { items.remove(it) }
    }

    override fun toString() = "LogContext [${items.size}]"
}

suspend fun logContext(vararg items: Pair<String, String>): CoroutineContext {
    val allItems = coroutineContext[LogContext]
        ?.getAll()?.toMutableMap()
        ?: mutableMapOf()
    allItems.putAll(items)
    return LogContext(allItems)
}

suspend fun addToContext(vararg items: Pair<String, String>) {
    coroutineContext[LogContext]?.putItems(*items)
}

suspend fun removeFromContext(vararg keys: String) {
    coroutineContext[LogContext]?.removeItem(*keys)
}
