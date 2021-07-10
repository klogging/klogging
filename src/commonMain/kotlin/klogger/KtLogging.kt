package klogger

import kotlin.reflect.KClass

expect fun classNameOf(ownerClass: KClass<*>): String?

val loggers: MutableMap<String, Klogger> = mutableMapOf()

internal fun loggerFor(name: String?): Klogger {
    val loggerName = name ?: "KtLogging"
    return loggers.getOrPut(loggerName) { BaseLogger(loggerName) }
}

fun logger(name: String): Klogger = loggerFor(name)

fun logger(ownerClass: KClass<*>): Klogger = loggerFor(classNameOf(ownerClass))

interface KtLogging {
    val logger: Klogger
        get() = loggerFor(classNameOf(this::class))
}
