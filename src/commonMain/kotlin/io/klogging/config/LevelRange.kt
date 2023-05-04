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

package io.klogging.config

import io.klogging.FlowList
import io.klogging.Level

/** Inclusive range of logging levels with the names of sinks where events will be sent. */
public data class LevelRange(
    val minLevel: Level,
    val maxLevel: Level
) : ClosedRange<Level> {
    override val start: Level get() = minLevel
    override val endInclusive: Level get() = maxLevel

    internal val sinkNames = FlowList<String>()

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
