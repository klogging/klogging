package ktlogging

import kotlin.reflect.KClass

expect fun classNameOf(ownerClass: KClass<*>): String?

val LOGGERS: MutableMap<String, Ktlogger> = mutableMapOf()

internal fun loggerFor(name: String?): Ktlogger {
    val loggerName = name ?: "KtLogging"
    return LOGGERS.getOrPut(loggerName) { BaseLogger(loggerName) }
}

fun logger(name: String): Ktlogger = loggerFor(name)

fun logger(ownerClass: KClass<*>): Ktlogger = loggerFor(classNameOf(ownerClass))

interface KtLogging {
    val logger: Ktlogger
        get() = loggerFor(classNameOf(this::class))
}
