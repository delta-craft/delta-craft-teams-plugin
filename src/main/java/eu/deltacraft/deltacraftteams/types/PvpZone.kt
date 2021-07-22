package eu.deltacraft.deltacraftteams.types

import org.bukkit.Location
import java.util.*
import kotlin.math.max
import kotlin.math.min

data class PvpZone(val firstPoint: Location, val secondPoint: Location, val name: String) {
    val worldUniqueId: UUID = firstPoint.world.uid

    val maxX = max(firstPoint.blockX, secondPoint.blockX)
    val maxZ = max(firstPoint.blockZ, secondPoint.blockZ)

    val minX = min(firstPoint.blockX, secondPoint.blockX)
    val minZ = min(firstPoint.blockZ, secondPoint.blockZ)

    val mainY = max(firstPoint.blockY, secondPoint.blockY)

    fun contains(loc: Location): Boolean {
        return loc.world.uid == worldUniqueId && contains(loc.blockX, loc.blockZ)
    }

    private fun contains(x: Int, z: Int): Boolean {
        return x in minX..maxX && z in minZ..maxZ
    }
}