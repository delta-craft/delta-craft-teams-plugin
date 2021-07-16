package eu.deltacraft.deltacraftteams.types.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*

object DateSerializer : KSerializer<Date> {
    override fun deserialize(decoder: Decoder): Date {
        return Date(decoder.decodeLong())
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Date", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeLong(value.time)
    }
}