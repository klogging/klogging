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

package io.klogging.java;

import io.klogging.NoCoLogger;

import static io.klogging.NoCoLoggingKt.noCoLogger;

public class LoggerFactory {

    /**
     * Returns an instance of {@link NoCoLogger} with the specified name.
     *
     * <p>
     * Example usage:
     * <code>
     * public class ThingDoer {
     * private static final NoCoLogger auditLogger = LoggerFactory.getLogger("auditing");
     * <p>
     * public void doThing() {
     * auditLogger.info("Doing thing");
     * }
     * }
     * </code>
     * </p>
     *
     * @param loggerName name of the logger
     * @return a new or existing instance with that name
     */
    public static NoCoLogger getLogger(String loggerName) {
        return noCoLogger(loggerName);
    }

    /**
     * Returns an instance of {@link NoCoLogger} with the name of the
     * specified class.
     * <p>
     * Example usage:
     * <code>
     * public class ImportantService {
     * private static final NoCoLogger logger = LoggerFactory.getLogger(ImportantService.class);
     * <p>
     * public ReturnType doStuff(ArgType arg) {
     * logger.debug("Called doStuff with {arg}", arg);
     * // do stuff
     * }
     * }
     * </code>
     * </p>
     *
     * @param loggerClass class whose name if for the logger
     * @return a new or existing instance with that class name
     */
    public static NoCoLogger getLogger(Class<?> loggerClass) {
        return noCoLogger(loggerClass.getName());
    }
}
