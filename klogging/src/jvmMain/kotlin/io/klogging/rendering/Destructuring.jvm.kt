/*

   Copyright 2021-2025 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.rendering

import io.klogging.events.EventItems
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

internal const val typeKey = "\$type"

internal actual fun destructure(obj: Any): EventItems = buildMap {
    obj::class.memberProperties.forEach { property ->
        property.isAccessible = true
        val objClass = property.returnType.classifier as? KClass<*> ?: return@forEach
        val name = property.name
        val value = property.getter.call(obj)
        if (value == null) put(name, null)
        when (objClass) {
            String::class,
            Int::class,
            Long::class,
            Float::class,
            Double::class,
            List::class,
            Set::class,
            Map::class,
            Boolean::class -> put(name, value)

            else -> put(name, destructure(value!!))
        }
    }
    put(typeKey, obj::class.simpleName)
}
