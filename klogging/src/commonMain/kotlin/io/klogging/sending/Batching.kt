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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.whileSelect

/**
 * Receive a batch of items from a channel, up to time and batch size limits.
 *
 * @param channel the channel to receive items from
 * @param maxTimeMillis maximum time in milliseconds to wait for items
 * @param maxSize maximum batch size
 *
 * @return the list of items in the order received
 */
@OptIn(ExperimentalCoroutinesApi::class)
public suspend fun <E> receiveBatch(
    channel: ReceiveChannel<E>,
    maxTimeMillis: Long,
    maxSize: Int,
): List<E> {
    val batch = mutableListOf<E>()
    whileSelect {
        onTimeout(maxTimeMillis) { false }
        channel.onReceiveCatching { result ->
            result.onFailure { if (it != null) throw it }
                .onClosed { return@onReceiveCatching false }
                .onSuccess { batch += it }
            batch.size < maxSize
        }
    }
    return batch
}
