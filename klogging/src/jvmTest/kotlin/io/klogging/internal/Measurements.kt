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

import io.klogging.Level.INFO
import io.klogging.Level.WARN
import io.klogging.config.loggingConfiguration
import io.klogging.logger
import io.klogging.rendering.RENDER_ANSI
import io.klogging.sending.STDOUT
import kotlin.random.Random
import kotlin.system.measureTimeMillis


fun main() {
    repeat(10_000) { logger("test.Logger-$it") }
    repeat(10_000) { logger("test.sub.Logger-$it") }
    loggingConfiguration {
        sink("stdout", RENDER_ANSI, STDOUT)
        logging {
            fromLoggerBase("test")
            fromMinLevel(INFO) { toSink("stdout") }
        }
        logging {
            fromLoggerBase("org.apache")
            fromMinLevel(WARN) { toSink("stdout") }
        }
    }
    val numRuns = 100
    var totalTime = 0L
    repeat(numRuns) {
        val howLong = measureTimeMillis {
            repeat(10_000) {
                val id = Random.nextLong(10_000)
                repeat(10_000) {
                    Dispatcher.cachedSinksFor("dev.Logger-$id", INFO)
                }
            }
        }
        totalTime += howLong
        println("$howLong")
    }
    println("Average time: ${totalTime / numRuns}")
}