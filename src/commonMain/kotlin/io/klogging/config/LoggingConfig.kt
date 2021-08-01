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

import io.klogging.events.Level

/**
 * Inclusive range of logging levels with the names of sinks where events will be dispatched.
 */
public data class LevelRange(
    val minLevel: Level,
    val maxLevel: Level,
) {
    internal val sinkNames = mutableListOf<String>()

    /**
     * Adds a sink to the list for this range of levels.
     */
    public fun toSink(sinkName: String) {
        if (KloggingConfiguration.sinks.containsKey(sinkName)) sinkNames.add(sinkName)
        // TODO create an internal console logger for this and similar messages.
        else println("WARN: sink $sinkName has not been defined and will be ignored")
    }
}

/**
 * Logging configuration with a logger name match and a list of level ranges that
 * maps to a list of sinks for each.
 */
public class LoggingConfig {

    /**
     * Default matching is all logger names.
     */
    internal val matchAllLoggers: String = ".*"

    internal var nameMatch: Regex = Regex(matchAllLoggers)
    internal val ranges = mutableListOf<LevelRange>()

    /** Match logger names from this base. */
    public fun fromLoggerBase(name: String) {
        nameMatch = Regex("^$name.*")
    }

    /** Match this logger name exactly. */
    public fun exactLogger(name: String) {
        nameMatch = Regex("^$name\$")
    }

    /**
     *  DSL: for a range of levels from `minLevel` to `FATAL`.
     */
    public fun fromMinLevel(minLevel: Level, block: LevelRange.() -> Unit) {
        val range = LevelRange(minLevel, Level.FATAL)
        range.apply(block)
        if (range.sinkNames.isNotEmpty()) ranges.add(range)
    }

    /**
     * DSL: at a specific level.
     */
    public fun atLevel(level: Level, block: LevelRange.() -> Unit) {
        val range = LevelRange(level, level)
        range.apply(block)
        if (range.sinkNames.isNotEmpty()) ranges.add(range)
    }
}
