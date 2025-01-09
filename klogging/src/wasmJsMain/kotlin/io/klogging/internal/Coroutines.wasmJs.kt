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
package io.klogging.internal

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual fun sendingContext(): CoroutineContext {
    debug("Coroutines", "Creating sending context for Klogging using Dispatchers.Unconfined")
    return Dispatchers.Unconfined
}
