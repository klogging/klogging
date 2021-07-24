package ktlogging

import ktlogging.impl.KtLoggerImpl
import kotlin.reflect.KClass

public expect fun classNameOf(ownerClass: KClass<*>): String?

private val LOGGERS: MutableMap<String, KtLogger> = mutableMapOf()

internal fun loggerFor(name: String?): KtLogger {
    val loggerName = name ?: "KtLogging"
    return LOGGERS.getOrPut(loggerName) { KtLoggerImpl(loggerName) }
}

public fun logger(name: String): KtLogger = loggerFor(name)

public fun logger(ownerClass: KClass<*>): KtLogger = loggerFor(classNameOf(ownerClass))

public interface KtLogging {
    public val logger: KtLogger
        get() = loggerFor(classNameOf(this::class))
}
