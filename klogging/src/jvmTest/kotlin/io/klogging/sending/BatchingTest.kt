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

package io.klogging.sending

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BatchingTest : DescribeSpec({
    describe("`receiveBatch()` function") {
        it("returns an empty list if no items are sent in the time") {
            val channel = Channel<Int>()
            val batch = receiveBatch(channel, maxTimeMillis = 10, maxSize = 10)

            batch shouldHaveSize 0
        }
        it("returns items fewer than `maxSize` in a single batch") {
            val channel = Channel<Int>(10)
            repeat(5) { channel.send(it) }
            val batch = receiveBatch(channel, maxTimeMillis = 50, maxSize = 10)

            batch shouldContainExactly listOf(0, 1, 2, 3, 4)
        }
        it("returns items more than `maxSize` in multiple batches") {
            val channel = Channel<Int>(10)
            repeat(9) { channel.send(it) }
            val batches = listOf(
                receiveBatch(channel, maxTimeMillis = 50, maxSize = 5),
                receiveBatch(channel, maxTimeMillis = 50, maxSize = 5),
            )

            batches shouldContainExactly listOf(
                listOf(0, 1, 2, 3, 4),
                listOf(5, 6, 7, 8),
            )
        }
        it("returns items in multiple batches if they are sent over `maxTimeMillis`") {
            val channel = Channel<Int>(10)
            launch {
                repeat(5) { channel.send(it) }
                delay(70)
                repeat(5) { channel.send(it + 5) }
            }
            val batches = listOf(
                receiveBatch(channel, maxTimeMillis = 50, maxSize = 10),
                receiveBatch(channel, maxTimeMillis = 50, maxSize = 10),
            )

            batches shouldContainExactly listOf(
                listOf(0, 1, 2, 3, 4),
                listOf(5, 6, 7, 8, 9),
            )
        }
    }
})
