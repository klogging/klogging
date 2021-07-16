package ktlogging

import ktlogging.impl.KtLoggerImpl
import kotlin.reflect.KClass

expect fun classNameOf(ownerClass: KClass<*>): String?

val LOGGERS: MutableMap<String, KtLogger> = mutableMapOf()

internal fun loggerFor(name: String?): KtLogger {
    val loggerName = name ?: "KtLogging"
    return LOGGERS.getOrPut(loggerName) { KtLoggerImpl(loggerName) }
}

fun logger(name: String): KtLogger = loggerFor(name)

fun logger(ownerClass: KClass<*>): KtLogger = loggerFor(classNameOf(ownerClass))

interface KtLogging {
    val logger: KtLogger
        get() = loggerFor(classNameOf(this::class))
}
