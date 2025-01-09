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

import io.klogging.events.LogEvent
import io.klogging.rendering.RENDER_CLEF
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.rendering.RenderString
import io.klogging.sending.STDERR
import io.klogging.sending.STDOUT
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.shouldBe

class ConfigurationExtensionsTest : DescribeSpec({
    describe("`SinkConfiguration.updateRenderer` function") {
        it("sets the renderer to RENDER_CLEF when the name is CLEF") {
            val sinkConfig = SinkConfiguration(RENDER_SIMPLE, STDOUT)
            sinkConfig.updateRenderer("CLEF").renderer shouldBe RENDER_CLEF
        }
        it("does not set the renderer when the name does not match a built-in one") {
            val testRenderer: RenderString = object : RenderString {
                override fun invoke(event: LogEvent): String = "Test"
            }
            val sinkConfig = SinkConfiguration(testRenderer, STDERR)
            sinkConfig.updateRenderer("JUNK").renderer shouldBe testRenderer
        }
    }
    describe("`KloggingConfiguration.updateFromEnvironment` function") {
        it("updates the output format when the sink and renderer names match") {
            withEnvironment("KLOGGING_OUTPUT_FORMAT_STDOUT" to "CLEF") {
                val config = KloggingConfiguration()
                config.sinks["stdout"] = SinkConfiguration(RENDER_SIMPLE, STDOUT)
                config.updateFromEnvironment().sinks["stdout"]?.renderer shouldBe RENDER_CLEF
            }
        }
        it("does not update the output format when the sink name does not match") {
            withEnvironment("KLOGGING_OUTPUT_FORMAT_STDERR" to "CLEF") {
                val config = KloggingConfiguration()
                config.sinks["stdout"] = SinkConfiguration(RENDER_SIMPLE, STDOUT)
                config.updateFromEnvironment().sinks["stdout"]?.renderer shouldBe RENDER_SIMPLE
            }
        }
        it("does not update the output format when the renderer name does not match") {
            withEnvironment("KLOGGING_OUTPUT_FORMAT_STDOUT" to "Test") {
                val config = KloggingConfiguration()
                config.sinks["stdout"] = SinkConfiguration(RENDER_SIMPLE, STDOUT)
                config.updateFromEnvironment().sinks["stdout"]?.renderer shouldBe RENDER_SIMPLE
            }
        }
    }
})
