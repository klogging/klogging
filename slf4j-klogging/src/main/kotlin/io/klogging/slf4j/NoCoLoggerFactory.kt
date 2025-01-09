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

package io.klogging.slf4j

import io.klogging.noCoLogger
import org.slf4j.ILoggerFactory
import org.slf4j.Logger

/**
 * Implementation of [ILoggerFactory] whose [getLogger] function constructs an [io.klogging.NoCoLogger]
 * wrapped in a [NoCoLoggerWrapper].
 */
public class NoCoLoggerFactory : ILoggerFactory {
    /**
     * Construct an [io.klogging.NoCoLogger] wrapped in a [NoCoLoggerWrapper].
     *
     * `NoCoLogger` requires a non-null name, so supply the default `Logger`.
     * [org.slf4j.LoggerFactory.getLogger] does not guarantee that [name] is not null.
     */
    override fun getLogger(name: String?): Logger = NoCoLoggerWrapper(noCoLogger(name ?: "Logger"))
}
