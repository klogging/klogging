package ktlogging

import kotlin.reflect.KClass

public actual fun classNameOf(ownerClass: KClass<*>): String? {
    val ownerName = ownerClass.java.name
    return if (ownerName.endsWith("\$Companion")) ownerName.substringBeforeLast('$') else ownerName
}
