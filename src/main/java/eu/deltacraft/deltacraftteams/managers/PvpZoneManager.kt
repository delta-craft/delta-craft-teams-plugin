package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneCacheManager
import eu.deltacraft.deltacraftteams.managers.templates.CacheConfigManager
import eu.deltacraft.deltacraftteams.types.KeyHelper
import eu.deltacraft.deltacraftteams.types.PvpZone
import eu.deltacraft.deltacraftteams.types.TempZone
import org.bukkit.Location
import java.util.UUID


class PvpZoneManager(plugin: DeltaCraftTeams, cacheManager: PvpZoneCacheManager) :
    CacheConfigManager<PvpZoneCacheManager>(plugin, "pvp.yml", cacheManager) {
    constructor(plugin: DeltaCraftTeams) : this(plugin, PvpZoneCacheManager())

    private val mapManager = BlueMapManager()

    companion object {
        const val PvpZonesPrefix = "zones"
        const val PointOneKey = "pointOne"
        const val PointTwoKey = "pointTwo"
        const val TempKey = "temp"
    }


    override fun loadCache() {
        val regions = getZones()
        cacheManager.putAll(regions)
    }

    fun zoneExists(name: String): Boolean {
        return config.contains("${PvpZonesPrefix}.$name")
    }

    private fun getZones(): HashMap<String, PvpZone> {
        val section = config.getConfigurationSection(PvpZonesPrefix) ?: return HashMap()
        val keys = section.getKeys(false)
        if (keys.size < 1) {
            return HashMap()
        }
        val zones = HashMap<String, PvpZone>()
        for (key in keys) {
            val kh = KeyHelper(key, PvpZonesPrefix)
            val one = config.getLocation(kh[PointOneKey])
            val two = config.getLocation(kh[PointTwoKey])
            if (one == null || two == null) {
                return HashMap()
            }
            val region = PvpZone(one, two, key)
            zones[key] = region
        }
        return zones
    }

    fun addZone(one: Location, two: Location, name: String) {
        val keys = KeyHelper(name, PvpZonesPrefix)
        val keyOne = keys[PointOneKey]
        val keyTwo = keys[PointTwoKey]

        config[keyOne] = one
        config[keyTwo] = two
        saveConfig()

        val zone = PvpZone(one, two, name)

        cacheManager.addItem(zone)

        mapManager.addZoneToMap(zone)
    }

    fun removeZone(name: String) {
        val kh = KeyHelper(name, PvpZonesPrefix)
        config[kh.key] = null
        saveConfig()

        cacheManager.remove(name)

        if (cacheManager.containsKey(name)) {
            config[PvpZonesPrefix] = null
        }

        mapManager.removeZoneFromMap(name)
    }

    fun getTempZone(playerId: UUID): TempZone {
        return cacheManager.getTempZone(playerId)
    }

    fun saveTempLocation(playerId: UUID, loc: Location, first: Boolean = true) {
        cacheManager.setTempLocation(playerId, first, loc)
    }

    fun clearTempLocations(playerId: UUID) {
        cacheManager.removeTempLocation(playerId)
    }

    fun pointsAreSet(playerId: UUID): Boolean {
        return getTempZone(playerId).isSet
    }
}