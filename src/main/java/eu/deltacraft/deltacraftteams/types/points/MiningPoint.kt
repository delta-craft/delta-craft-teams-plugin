package eu.deltacraft.deltacraftteams.types.points

import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import org.bukkit.Material
import java.util.*

class MiningPoint(
    points: Int,
    playerUid: UUID,
    val material: Material,
    val tool: String,
    val totalDrops: Int,
    val count: Int = 1,
    val drops: MutableList<Int> = mutableListOf(),
) : Point(points, playerUid, PointType.Mining, "Vykopán blok ${material.name} (${count}x)") {
}