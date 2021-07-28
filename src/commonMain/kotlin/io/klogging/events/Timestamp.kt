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

package io.klogging.events

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public expect fun now(): Timestamp
public expect fun iso(timestamp: Timestamp): String

@Serializable(with = TimestampSerializer::class)
public data class Timestamp(val epochSeconds: Long, val nanos: Long) {
    override fun toString(): String = iso(this)
}

public object TimestampSerializer : KSerializer<Timestamp> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Timestamp", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Timestamp {
        val (epochSeconds, nanos) = decoder.decodeString().split(".")
        return Timestamp(epochSeconds.toLong(), nanos.toLong())
    }
}
