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

/**
 * A multiplatform, thread-safe [MutableList], implemented using a [MutableStateFlow] delegate.
 */
internal class FlowList<E>(
    vararg elements: E
) : MutableList<E> {

    private val _listFlow = MutableStateFlow(mutableListOf(*elements))

    override val size: Int
        get() = _listFlow.value.size

    override fun clear(): Unit = _listFlow.value.clear()

    override fun addAll(elements: Collection<E>): Boolean = _listFlow.value.addAll(elements)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = _listFlow.value.addAll(index, elements)

    override fun add(index: Int, element: E) = _listFlow.value.add(index, element)

    override fun add(element: E): Boolean = _listFlow.value.add(element)

    override fun get(index: Int): E = _listFlow.value.get(index)

    override fun isEmpty(): Boolean = _listFlow.value.isEmpty()

    override fun iterator(): MutableIterator<E> = _listFlow.value.iterator()

    override fun listIterator(): MutableListIterator<E> = _listFlow.value.listIterator()

    override fun listIterator(index: Int): MutableListIterator<E> = _listFlow.value.listIterator(index)

    override fun removeAt(index: Int): E = _listFlow.value.removeAt(index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = _listFlow.value.subList(fromIndex, toIndex)

    override fun set(index: Int, element: E): E = _listFlow.value.set(index, element)

    override fun retainAll(elements: Collection<E>): Boolean = _listFlow.value.retainAll(elements)

    override fun removeAll(elements: Collection<E>): Boolean = _listFlow.value.removeAll(elements)

    override fun remove(element: E): Boolean = _listFlow.value.remove(element)

    override fun lastIndexOf(element: E): Int = _listFlow.value.lastIndexOf(element)

    override fun indexOf(element: E): Int = _listFlow.value.indexOf(element)

    override fun containsAll(elements: Collection<E>): Boolean = _listFlow.value.containsAll(elements)

    override fun contains(element: E): Boolean = _listFlow.value.contains(element)
}
