package io.klogging

import kotlin.reflect.KClass

/**
 * Get the name of a class.
 *
 * @param ownerClass a [KClass] whose name is needed
 * @return the name of the class, if found
 *
 * Notes:
 * 1. The companion class can have a different name than "$Companion"
 * 2. Kotlin reflection is only supported for JVM, not for JS
 */
internal actual fun classNameOf(ownerClass: KClass<*>): String? = ownerClass.qualifiedName

