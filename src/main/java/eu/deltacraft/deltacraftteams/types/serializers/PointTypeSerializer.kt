package eu.deltacraft.deltacraftteams.types.serializers

import eu.deltacraft.deltacraftteams.utils.enums.PointType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PointTypeSerializer : KSerializer<PointType> {
    override fun deserialize(decoder: Decoder): PointType {
        return PointType.from(decoder.decodeInt()) ?: PointType.Mining
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("PointType", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: PointType) {
        encoder.encodeInt(value.id)
    }
}