package ktlogging

import ktlogging.impl.NoCoLoggerImpl
import kotlin.reflect.KClass

private val NOCO_LOGGERS: MutableMap<String, NoCoLogger> = mutableMapOf()

internal fun noCoLoggerFor(name: String?): NoCoLogger {
    val loggerName = name ?: "KtLogging"
    return NOCO_LOGGERS.getOrPut(loggerName) { NoCoLoggerImpl(loggerName) }
}

public fun noCoLogger(name: String): NoCoLogger = noCoLoggerFor(name)

public fun noCoLogger(ownerClass: KClass<*>): NoCoLogger = noCoLoggerFor(classNameOf(ownerClass))

public interface NoCoLogging {
    public val logger: NoCoLogger
        get() = noCoLoggerFor(classNameOf(this::class))
}
