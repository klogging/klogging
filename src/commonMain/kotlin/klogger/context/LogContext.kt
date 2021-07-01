package klogger.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class LogContext : AbstractCoroutineContextElement(LogContext) {

    companion object Key : CoroutineContext.Key<LogContext>

    private val objects = mutableMapOf<String, Any>()

    internal fun addObject(obj: Pair<String, Any>) = objects.put(obj.first, obj.second)

    internal fun addAll(objs: Map<String, Any>) = objects.putAll(objs)

    fun get(name: String) = objects[name]

    fun getAll() = objects.toMap()

    override fun toString() = "LogContext [${objects.size}]"
}

suspend fun logContext(vararg objects: Pair<String, Any>): CoroutineContext {
    val context = LogContext()

    coroutineContext[LogContext]?.let {
        context.addAll(it.getAll())
    }

    context.addAll(objects.toMap())

    return context
}
