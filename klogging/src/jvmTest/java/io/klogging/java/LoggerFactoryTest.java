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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.klogging.HelpersKt.randomString;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LoggerFactory class")
class LoggerFactoryTest {

    @Nested
    @DisplayName("getLogger() static function")
    class GetLogger {

        @Test
        @DisplayName("returns a `NoCoLogger` instance from a name")
        public void fromName() {
            String loggerName = randomString();
            NoCoLogger logger = LoggerFactory.getLogger(loggerName);

            assertEquals(loggerName, logger.getName());
        }

        @Test
        @DisplayName("returns the same `NoCoLogger` instance for the same name")
        public void sameInstance() {
            String loggerName = randomString();
            NoCoLogger firstLogger = LoggerFactory.getLogger(loggerName);
            NoCoLogger secondLogger = LoggerFactory.getLogger(loggerName);

            assertEquals(secondLogger, firstLogger);
        }

        @Test
        @DisplayName("returns a `NoCoLogger` instance from a class")
        public void fromClass() {
            class ClassWithLogger {
            }
            String className = ClassWithLogger.class.getName();
            NoCoLogger logger = LoggerFactory.getLogger(ClassWithLogger.class);

            assertEquals(className, logger.getName());
        }
    }
}
