/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package org.slf4j.impl

import org.slf4j.helpers.BasicMDCAdapter
import org.slf4j.spi.MDCAdapter

/**
 * Implementation of [org.slf4j.impl.StaticMDCBinder] to provide an [MDCAdapter].
 * It returns an instance of the SLF4J [BasicMDCAdapter], which provides basic
 * MDC handling.
 *
 * I believe this class must be in the `org.slf4j.impl` package for versions of SLF4J
 * before 1.8.
 */
public class StaticMDCBinder private constructor() {

    public companion object {
        /** Instance of this class available to Java via `StaticMDCBinder.getSingleton()`. */
        @JvmStatic
        public val singleton: StaticMDCBinder = StaticMDCBinder()
    }

    /**
     * Called only during SLF4J MDC construction.
     */
    public fun getMDCA(): MDCAdapter = BasicMDCAdapter()

    public fun getMDCAdapterClassStr(): String = BasicMDCAdapter::class.java.name
}
