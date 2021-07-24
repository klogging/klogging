package ktlogging.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

public class LogContext internal constructor(
    private val items: MutableMap<String, Any?>
) : AbstractCoroutineContextElement(LogContext) {

    public companion object Key : CoroutineContext.Key<LogContext>

    public fun get(key: String): Any? = items[key]

    public fun getAll(): Map<String, Any?> = items.toMap()

    internal fun putItems(vararg newItems: Pair<String, Any?>) {
        items.putAll(newItems)
    }

    internal fun removeItem(vararg keys: String) {
        keys.forEach { items.remove(it) }
    }

    override fun toString(): String = "LogContext [${items.size}]"
}

public suspend fun logContext(vararg items: Pair<String, Any?>): CoroutineContext {
    val allItems = coroutineContext[LogContext]
        ?.getAll()?.toMutableMap()
        ?: mutableMapOf()
    allItems.putAll(items)
    return LogContext(allItems)
}

public suspend fun addToContext(vararg items: Pair<String, Any?>) {
    coroutineContext[LogContext]?.putItems(*items)
}

public suspend fun removeFromContext(vararg keys: String) {
    coroutineContext[LogContext]?.removeItem(*keys)
}
