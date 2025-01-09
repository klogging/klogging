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

package io.klogging.jpl

import io.klogging.noCoLogger
import java.lang.System.Logger
import java.lang.System.LoggerFinder

/**
 * JDK Platform Logging service provider implementation for Klogging.
 */
public class KloggingLoggerFinder : LoggerFinder() {
    /**
     * Get a named logger.
     *
     * @param name name to identify the logger
     * @param module Java module for which the logger is being requested; CURRENTLY IGNORED
     * @return a wrapped Klogging logger with the specified name
     */
    override fun getLogger(name: String, module: Module): Logger =
        NoCoLoggerWrapper(noCoLogger(name))
}
