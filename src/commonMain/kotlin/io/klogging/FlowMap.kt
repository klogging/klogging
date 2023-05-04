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

package io.klogging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * A multiplatform, thread-safe [MutableMap], implemented using a [MutableStateFlow].
 */
internal class FlowMap<K, V>(
    vararg pairs: Pair<K, V>
) : MutableMap<K, V> {

    private val _mapFlow = MutableStateFlow(mutableMapOf(*pairs))

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>> = _mapFlow.value.entries
    override val keys: MutableSet<K> = _mapFlow.value.keys
    override val size: Int = _mapFlow.value.size
    override val values: MutableCollection<V> = _mapFlow.value.values

    override fun clear() = _mapFlow.update { it.apply { clear() } }

    override fun isEmpty(): Boolean = _mapFlow.value.isEmpty()

    override fun remove(key: K): V? {
        var previousValue: V? = null
        _mapFlow.update { it.apply { previousValue = remove(key) } }
        return previousValue
    }

    override fun putAll(from: Map<out K, V>) = _mapFlow.update { it.apply { putAll(from) } }

    override fun put(key: K, value: V): V? {
        var previousValue: V? = null
        _mapFlow.update { it.apply { previousValue = put(key, value) } }
        return previousValue
    }

    override fun get(key: K): V? = _mapFlow.value.get(key)

    override fun containsValue(value: V): Boolean = _mapFlow.value.containsValue(value)

    override fun containsKey(key: K): Boolean = _mapFlow.value.containsKey(key)
}
