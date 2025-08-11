/*

   Copyright 2021-2025 Victor Kuzmin and Michael Strasser.

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

package io.klogging.sending

import android.util.Log
import io.klogging.Level
import io.klogging.rendering.evalTemplate
import io.klogging.rendering.itemsAndStackTrace

public val ANDROID: EventSender =
    EventSender { batch ->
        batch.forEach { event ->
            val level =
                when (event.level) {
                    Level.TRACE -> Log.VERBOSE
                    Level.DEBUG -> Log.DEBUG
                    Level.INFO -> Log.INFO
                    Level.WARN -> Log.WARN
                    Level.ERROR -> Log.ERROR
                    Level.FATAL -> Log.ASSERT
                    Level.NONE -> Log.VERBOSE
                }

            val msg =
                buildString {
                    append("[${event.context}] ${event.evalTemplate()}")
                    append(event.itemsAndStackTrace)
                }

            Log.println(level, event.logger, msg)
        }
    }
