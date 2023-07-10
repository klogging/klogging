/*

   Copyright 2021-2023 Michael Strasser.

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

import io.klogging.slf4j.NoCoLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

/**
 * Implementation of [org.slf4j.impl.StaticLoggerBinder] to provide an [ILoggerFactory].
 *
 * I believe this class must be in the `org.slf4j.impl` package for versions of SLF4J
 * before 1.8.
 */
public class StaticLoggerBinder internal constructor(
    private val loggerFactory: ILoggerFactory,
) : LoggerFactoryBinder {

    public companion object {
        /** Version of SLF4J used to create this binding. */
        public const val REQUEST_API_VERSION: String = "1.7.32"

        /** Instance of this class available to Java via `StaticLoggingBinder.getSingleton()`. */
        @JvmStatic
        public val singleton: StaticLoggerBinder = StaticLoggerBinder(NoCoLoggerFactory())
    }

    private val loggerFactoryClassString = NoCoLoggerFactory::class.java.name

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    override fun getLoggerFactoryClassStr(): String = loggerFactoryClassString
}
