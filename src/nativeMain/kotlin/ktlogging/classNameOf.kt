package ktlogging

import kotlin.reflect.KClass

actual fun classNameOf(ownerClass: KClass<*>): String? =
    ownerClass::class.qualifiedName
