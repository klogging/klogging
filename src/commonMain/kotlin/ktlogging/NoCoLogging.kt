package ktlogging

import ktlogging.impl.NoCoLoggerImpl
import kotlin.reflect.KClass

val NOCO_LOGGERS: MutableMap<String, NoCoLogger> = mutableMapOf()

internal fun noCoLoggerFor(name: String?): NoCoLogger {
    val loggerName = name ?: "KtLogging"
    return NOCO_LOGGERS.getOrPut(loggerName) { NoCoLoggerImpl(loggerName) }
}

fun noCoLogger(name: String): NoCoLogger = noCoLoggerFor(name)

fun noCoLogger(ownerClass: KClass<*>): NoCoLogger = noCoLoggerFor(classNameOf(ownerClass))

interface NoCoLogging {
    val logger: NoCoLogger
        get() = noCoLoggerFor(classNameOf(this::class))
}
