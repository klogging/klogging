package ktlogging

import kotlin.reflect.KClass

val JLOGGERS: MutableMap<String, JLogger> = mutableMapOf()

internal fun jLoggerFor(name: String?): JLogger {
    val loggerName = name ?: "KtLogging"
    return JLOGGERS.getOrPut(loggerName) { JavaLogger(loggerName) }
}

fun jLogger(name: String): JLogger = jLoggerFor(name)

fun jLogger(ownerClass: KClass<*>): JLogger = jLoggerFor(classNameOf(ownerClass))

interface JavaLogging {
    val logger: JLogger
        get() = jLoggerFor(classNameOf(this::class))
}
