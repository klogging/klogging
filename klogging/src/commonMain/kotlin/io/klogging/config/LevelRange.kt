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

package io.klogging.config

import io.klogging.AtomicMutableList
import io.klogging.Level
import io.klogging.internal.warn

/**
 * Inclusive range of logging levels with the names of sinks where events will be sent.
 *
 * @property minLevel inclusive minimum level of this range
 * @property maxLevel inclusive maximum level of this range
 */
@Suppress("DataClassPrivateConstructor")
public data class LevelRange private constructor(
    val minLevel: Level,
    val maxLevel: Level,
) : ClosedRange<Level> {

    public companion object {
        /**
         * Operator invoke function that ensures minimum and maximum levels are ordered correctly.
         *
         * @param min minimum level
         * @param max maximum level
         */
        public operator fun invoke(min: Level, max: Level): LevelRange =
            if (min > max) {
                LevelRange(max, min).also {
                    warn("Configuration", "LevelRange maxLevel must be greater than minLevel. Constructing as $it")
                }
            } else {
                LevelRange(min, max)
            }
    }

    public override val start: Level get() = minLevel
    public override val endInclusive: Level get() = maxLevel

    /** List of current sink names. */
    internal val sinkNames: AtomicMutableList<String> = AtomicMutableList()

    /**
     * DSL function to specify a sink where events for this [LoggingConfig] should be sent.
     *
     * @param sinkName name of the sink
     */
    @ConfigDsl
    public fun toSink(sinkName: String) {
        sinkNames += sinkName
    }
}
