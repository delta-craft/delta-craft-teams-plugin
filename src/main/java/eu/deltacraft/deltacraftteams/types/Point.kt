package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.types.serializers.DateSerializer
import eu.deltacraft.deltacraftteams.types.serializers.PointTypeSerializer
import eu.deltacraft.deltacraftteams.types.serializers.UuidSerializer
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Location
import java.util.*

@Serializable
open class Point(
    val points: Int,
    @Serializable(with = UuidSerializer::class)
    @SerialName("uuid")
    val playerUid: UUID,
    @Serializable(with = PointTypeSerializer::class)
    @SerialName("pointType")
    val type: PointType,
    val description: String = "",
    @SerialName("pointTags")
    val tags: MutableList<PointTag> = mutableListOf(),
) {
    @Serializable(with = DateSerializer::class)
    val created = Date()

    fun addTag(key: String, value: String) {
        addTag(PointTag(key, value))
    }

    fun addTag(key: String, value: Long) {
        return addTag(key, value.toString())
    }

    fun addTag(key: String, value: Int) {
        return addTag(key, value.toString())
    }

    fun addTag(location: Location) {
        addTag("X", location.blockX)
        addTag("Y", location.blockY)
        addTag("Z", location.blockZ)
        addTag("World", location.world.name)
    }

    private fun addTag(tag: PointTag) {
        if (tags.all { x -> x.key != tag.key }) {
            tags.add(tag)
        }
    }

}