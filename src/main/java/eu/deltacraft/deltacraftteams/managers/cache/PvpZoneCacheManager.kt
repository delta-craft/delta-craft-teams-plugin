package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.TempCache
import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.PvpZone
import eu.deltacraft.deltacraftteams.types.TempZone
import org.bukkit.Location
import java.util.*

class PvpZoneCacheManager : CacheManager<String, PvpZone>(true) {
    private val tempCache = TempCache<UUID, TempZone>()

    fun addItem(zone: PvpZone) {
        this[zone.name] = zone
    }

    fun getPvpZoneNames(): List<String> {
        return values.map { x -> x.name }
    }

    operator fun get(l: Location): PvpZone? {
        for (region in this.values) {
            if (region.contains(l)) {
                return region
            }
        }
        return null
    }

    fun isInPvpZone(l: Location): Boolean {
        return this[l] != null
    }

    fun setTempLocation(playerId: UUID, first: Boolean, loc: Location) {
        loc.yaw = 0f
        loc.pitch = 0f

        val tempZone = tempCache.getOrCreate(playerId) { TempZone() }

        if (first) {
            tempZone.first = loc
        } else {
            tempZone.second = loc
        }

        tempCache[playerId] = tempZone
    }

    fun getTempLocation(playerId: UUID, first: Boolean): Location? {
        if (!tempCache.containsKey(playerId)) {
            return null
        }
        val tempZone = tempCache[playerId]!!

        return if (first) tempZone.first else tempZone.first
    }

    fun removeTempLocation(playerId: UUID) {
        tempCache.remove(playerId)
    }

    fun getTempZone(playerId: UUID): TempZone {
        return tempCache.getOrCreate(playerId) { TempZone() }
    }

}