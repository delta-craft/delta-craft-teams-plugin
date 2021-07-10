package eu.deltacraft.deltacraftteams.types

import org.bukkit.Location
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

data class PvpZone(val firstPoint: Location, val secondPoint: Location, val name: String) {
    val worldUniqueId: UUID = firstPoint.world.uid

    val maxX = ceil(max(firstPoint.x, secondPoint.x))
    val maxZ = ceil(max(firstPoint.z, secondPoint.z))

    val minX = floor(min(firstPoint.x, secondPoint.x))
    val minZ = floor(min(firstPoint.z, secondPoint.z))

    private val maxY = ceil(max(firstPoint.y, secondPoint.y))

    val mainY = maxY.toFloat()

    fun contains(loc: Location): Boolean {
        return loc.world.uid == worldUniqueId && loc.x > minX && loc.x <= maxX && loc.z > minZ && loc.z <= maxZ
    }
}