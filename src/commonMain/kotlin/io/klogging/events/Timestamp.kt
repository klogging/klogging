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
