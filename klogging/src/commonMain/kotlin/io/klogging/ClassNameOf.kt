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

package io.klogging

import kotlin.reflect.KClass

/**
 * Get the name of a class.
 *
 * @param ownerClass a [KClass] whose name is needed
 * @return the name of the class, if found
 */
@Suppress("KDOC_WITHOUT_PARAM_TAG") // Stop false positives
internal expect fun classNameOf(ownerClass: KClass<*>): String?
