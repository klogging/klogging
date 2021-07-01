package klogger.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class LogContext internal constructor(
    private val objects: Map<String, Any>
) : AbstractCoroutineContextElement(LogContext) {

    companion object Key : CoroutineContext.Key<LogContext>

    fun get(name: String) = objects[name]

    fun getAll() = objects.toMap()

    override fun toString() = "LogContext [${objects.size}]"
}

suspend fun logContext(vararg objects: Pair<String, Any>): CoroutineContext =
    LogContext(
        coroutineContext[LogContext]?.let {
            it.getAll() + objects.toMap()
        } ?: objects.toMap()
    )

