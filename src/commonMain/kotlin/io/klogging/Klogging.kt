package io.klogging

import io.klogging.impl.KloggerImpl
import kotlin.reflect.KClass

public expect fun classNameOf(ownerClass: KClass<*>): String?

private val LOGGERS: MutableMap<String, Klogger> = mutableMapOf()

internal fun loggerFor(name: String?): Klogger {
    val loggerName = name ?: "Klogging"
    return LOGGERS.getOrPut(loggerName) { KloggerImpl(loggerName) }
}

public fun logger(name: String): Klogger = loggerFor(name)

public fun logger(ownerClass: KClass<*>): Klogger = loggerFor(classNameOf(ownerClass))

public interface Klogging {
    public val logger: Klogger
        get() = loggerFor(classNameOf(this::class))
}
