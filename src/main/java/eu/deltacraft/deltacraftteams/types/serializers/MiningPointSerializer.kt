package eu.deltacraft.deltacraftteams.types.serializers

import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.points.MiningPoint
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object MiningPointSerializer : KSerializer<MiningPoint> {
    override fun deserialize(decoder: Decoder): MiningPoint {
        throw NotImplementedError()
    }

    override val descriptor: SerialDescriptor
        get() = Point.serializer().descriptor

    override fun serialize(encoder: Encoder, value: MiningPoint) {
        val point = Point(value.points, value.playerUid, value.type, value.description)

        point.addTag("TotalDrops", value.totalDrops)
        point.addTag("From", value.start.toString())
        point.addTag("FromTimestamp", value.start.time)
        point.addTag("To", value.end.toString())
        point.addTag("ToTimestamp ", value.end.time)
        point.addTag("Block", value.material)
        point.addTag("Tool", value.tool)

        val drops = value.drops.joinToString("|")
        point.addTag("Drops", drops)

        encoder.encodeSerializableValue(Point.serializer(), point)
    }
}