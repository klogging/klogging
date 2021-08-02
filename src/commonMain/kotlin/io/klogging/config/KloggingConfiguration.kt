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

import io.klogging.dispatching.DispatchString
import io.klogging.events.Level
import io.klogging.render.RenderString

/**
 * Klogging configuration for a runtime.
 */
public object KloggingConfiguration {

    internal val sinks = mutableMapOf<String, SinkConfiguration>()
    internal val configs = mutableListOf<LoggingConfig>()

    /**
     * DSL function to specify a sink where log events can be dispatched.
     *
     * @param sinkName name used to refer to the sink
     * @param sinkConfig configuration to use
     */
    @ConfigDsl
    public fun sink(sinkName: String, sinkConfig: SinkConfiguration) {
        sinks[sinkName] = sinkConfig
    }

    /**
     * DSL function to specify a sink where log events can be dispatched.
     *
     * @param sinkName name used to refer to the sink
     * @param renderer object that renders an event into a string
     * @param dispatcher object that sends an event as string somewhere
     */
    @ConfigDsl
    public fun sink(sinkName: String, renderer: RenderString, dispatcher: DispatchString) {
        sinks[sinkName] = SinkConfiguration(renderer, dispatcher)
    }

    /**
     * DSL function to add a logging configuration specified in [configBlog].
     */
    @ConfigDsl
    public fun logging(configBlog: LoggingConfig.() -> Unit) {
        val loggingConfig = LoggingConfig()
        loggingConfig.apply(configBlog)
        configs.add(loggingConfig)
    }

    /** Calculate the minimum level of all level ranges in all configurations. */
    public fun minimumLevelOf(loggerName: String): Level = configs
        .filter { it.nameMatch.matches(loggerName) }
        .flatMap { it.ranges }
        .minOfOrNull { it.minLevel } ?: Level.NONE

    /** Clear all configurations. */
    internal fun reset() {
        sinks.clear()
        configs.clear()
    }
}
