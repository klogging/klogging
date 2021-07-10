package klogger

import kotlin.reflect.KClass

actual fun classNameOf(ownerClass: KClass<*>): String? {
    val ownerName = ownerClass.java.name
    return if (ownerName.endsWith("\$Companion")) ownerName.substringBeforeLast('$') else ownerName
}