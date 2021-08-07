package eu.deltacraft.deltacraftteams.types.points

import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import java.util.*

class MiningPoint(
    points: Int,
    playerUid: UUID,
    val material: String,
    val tool: String,
    val totalDrops: Int,
    val count: Int = 1,
    val start: Date = Date(),
    private val end: Date = Date(),
) : Point(points, playerUid, PointType.Mining, "Vykopán blok $material (${count}x)") {

    fun isSimilar(other: MiningPoint): Boolean {
        return playerUid == other.playerUid &&
                material == other.material &&
                tool == other.tool
    }

    fun toPoint(): Point {
        val point = Point(this.points, this.playerUid, this.type, this.description)

        point.addTag("TotalDrops", this.totalDrops)
        point.addTag("From", this.start.toString())
        point.addTag("FromTimestamp", this.start.time)
        point.addTag("To", this.end.toString())
        point.addTag("ToTimestamp ", this.end.time)
        point.addTag("Block", this.material)
        point.addTag("Tool", this.tool)

        return point
    }


}