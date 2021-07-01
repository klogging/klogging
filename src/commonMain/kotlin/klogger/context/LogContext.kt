package klogger.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class LogContext internal constructor(
    private val items: Map<String, Any>
) : AbstractCoroutineContextElement(LogContext) {

    companion object Key : CoroutineContext.Key<LogContext>

    fun get(key: String) = items[key]

    fun getAll() = items.toMap()

    override fun toString() = "LogContext [${items.size}]"
}

suspend fun logContext(vararg items: Pair<String, Any>): CoroutineContext =
    LogContext(
        coroutineContext[LogContext]?.let {
            it.getAll() + items.toMap()
        } ?: items.toMap()
    )

