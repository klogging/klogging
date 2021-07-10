package klogger

import kotlin.reflect.KClass

actual fun classNameOf(ownerClass: KClass<*>): String? =
    ownerClass::class.simpleName