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

import io.klogging.Level
import io.klogging.Level.FATAL
import io.klogging.Level.TRACE

/** Function type for matching a logger name. */
internal typealias Matcher = (String) -> Boolean

/** Matcher that always returns `true`. */
internal val matchAll: Matcher = { true }

/**
 * Logging configuration with a logger name match and a list of level ranges that
 * maps to a list of sinks for each.
 */
public class LoggingConfig {

    /** Flag signifying that searching for  should stop after the first match. */
    internal var stopOnMatch: Boolean = false

    /** List of [LevelRange]s in this configuration. */
    internal val ranges: MutableList<LevelRange> = mutableListOf()

    /** Matcher in this configuration. */
    internal var nameMatcher: Matcher = matchAll

    /**
     * DSL function to specify that logger names should match from the specified base name.
     *
     * @param baseName match logger names from this base
     * @param stopOnMatch stop using loggers after this one if it matches
     */
    @ConfigDsl
    public fun fromLoggerBase(baseName: String, stopOnMatch: Boolean = false) {
        nameMatcher = { it.startsWith(baseName) }
        this.stopOnMatch = stopOnMatch
    }

    /**
     * DSL function to specify that logger names should match this name exactly.
     *
     * @param exactName match this logger name exactly
     * @param stopOnMatch stop using loggers after this one if it matches
     */
    @ConfigDsl
    public fun exactLogger(exactName: String, stopOnMatch: Boolean = false) {
        nameMatcher = { it == exactName }
        this.stopOnMatch = stopOnMatch
    }

    /**
     * DSL function to specify that logger names should match this regular
     * expression pattern.
     *
     * @param pattern match logger names using this pattern
     * @param stopOnMatch stop using loggers after this one if it matches
     */
    @ConfigDsl
    public fun matchLogger(pattern: String, stopOnMatch: Boolean = false) {
        nameMatcher = { Regex(pattern).matches(it) }
        this.stopOnMatch = stopOnMatch
    }

    /**
     * DSL function to specify the minimum level from which to log.
     *
     * @param minLevel inclusive minimum level
     * @param configBlock configuration for this range of levels
     */
    @ConfigDsl
    public fun fromMinLevel(
        minLevel: Level,
        configBlock: LevelRange.() -> Unit,
    ) {
        val range = LevelRange(minLevel, FATAL)
        range.apply(configBlock)
        if (range.sinkNames.isNotEmpty()) ranges.add(range)
    }

    /**
     * DSL function to specify the maximum level at which to log.
     *
     * @param maxLevel inclusive maximum level
     * @param configBlock configuration for this range of levels
     */
    @ConfigDsl
    public fun toMaxLevel(
        maxLevel: Level,
        configBlock: LevelRange.() -> Unit,
    ) {
        val range = LevelRange(TRACE, maxLevel)
        range.apply(configBlock)
        if (range.sinkNames.isNotEmpty()) ranges.add(range)
    }

    /**
     * DSL function to specify a specific level at which to log.
     *
     * @param level exact level
     * @param configBlock configuration for this range of levels
     */
    @ConfigDsl
    public fun atLevel(level: Level, configBlock: LevelRange.() -> Unit) {
        val range = LevelRange(level, level)
        range.apply(configBlock)
        if (range.sinkNames.isNotEmpty()) ranges.add(range)
    }

    /**
     * DSL function to specify an inclusive range of levels at which to log.
     *
     * @param minLevel lower level of inclusive range
     * @param maxLevel upper level of inclusive range
     * @param configBlock configuration for this range of levels
     */
    @ConfigDsl
    public fun inLevelRange(minLevel: Level, maxLevel: Level, configBlock: LevelRange.() -> Unit) {
        val range = LevelRange(minLevel, maxLevel)
        range.apply(configBlock)
        if (range.sinkNames.isNotEmpty()) ranges.add(range)
    }

    /**
     * DSL function to specify a sink where events for all logging levels should be sent.
     *
     * @param sinkName name of the sink
     */
    @ConfigDsl
    public fun toSink(sinkName: String) {
        val range = LevelRange(TRACE, FATAL)
        range.toSink(sinkName)
        if (range.sinkNames.isNotEmpty()) ranges.add(range)
    }
}
