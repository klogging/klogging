package io.klogging

import kotlin.reflect.KClass

public actual fun classNameOf(ownerClass: KClass<*>): String? =
    ownerClass::class.qualifiedName
