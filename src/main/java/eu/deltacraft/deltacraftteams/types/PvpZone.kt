package eu.deltacraft.deltacraftteams.types

import org.bukkit.Location
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

data class PvpZone(val firstPoint: Location, val secondPoint: Location, val name: String) {
    val worldUniqueId: UUID = firstPoint.world.uid

    private val maxX = ceil(max(firstPoint.x, secondPoint.x))
    private val maxZ = ceil(max(firstPoint.z, secondPoint.z))

    private val minX = floor(min(firstPoint.x, secondPoint.x))
    private val minZ = floor(min(firstPoint.z, secondPoint.z))

    fun contains(loc: Location): Boolean {
        return loc.world.uid == worldUniqueId && loc.x > minX && loc.x <= maxX && loc.z > minZ && loc.z <= maxZ
    }
}