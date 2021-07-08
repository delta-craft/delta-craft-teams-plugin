package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.PvpZone
import org.bukkit.Location

class PvpZoneCacheManager(plugin: DeltaCraftTeams) : CacheManager<String, PvpZone>(plugin, true) {
    private val tempCache: HashMap<String, Location> = HashMap()

    fun addItem(
        one: Location,
        two: Location,
        name: String
    ) {
        val region = PvpZone(one, two, name)
        this.addItem(region)
    }

    fun addItem(
        zone: PvpZone
    ) {
        this.addItem(zone.name, zone)
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

    fun setTempLocation(key: String, loc: Location) {
        loc.yaw = 0f
        loc.pitch = 0f

        tempCache[key] = loc
    }

    fun getTempLocation(key: String): Location? {
        if (!tempCache.containsKey(key)) {
            return null
        }
        return tempCache[key]
    }

}