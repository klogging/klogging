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

package io.klogging.config

/** Marker annotation for DSL functions. */
@DslMarker
public annotation class ConfigDsl

/**
 * Root DSL function for creating a [KloggingConfiguration].
 *
 * @param append if `true`, append this configuration to any existing one.
 *               Default is `false`, causing this configuration replace any existing one.
 */
@ConfigDsl
public fun loggingConfiguration(append: Boolean = false, block: KloggingConfiguration.() -> Unit) {
    if (!append) KloggingConfiguration.reset()
    KloggingConfiguration.apply(block)
}
