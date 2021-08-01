package eu.deltacraft.deltacraftteams.types.points

import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.serializers.MiningPointSerializer
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import kotlinx.serialization.Serializable
import java.util.*

@Serializable(with = MiningPointSerializer::class)
class MiningPoint(
    points: Int,
    playerUid: UUID,
    val material: String,
    val tool: String,
    val totalDrops: Int,
    val count: Int = 1,
    val start: Date = Date(),
    val end: Date = Date(),
    val drops: MutableList<Int> = mutableListOf(),
) : Point(points, playerUid, PointType.Mining, "Vykopán blok ${material} (${count}x)") {
}