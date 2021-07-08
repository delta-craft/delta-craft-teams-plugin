package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneCacheManager
import eu.deltacraft.deltacraftteams.managers.templates.CacheConfigManager
import eu.deltacraft.deltacraftteams.types.KeyHelper
import eu.deltacraft.deltacraftteams.types.PvpZone
import org.bukkit.Location
import java.util.UUID

class PvpZoneManager(plugin: DeltaCraftTeams, cacheManager: PvpZoneCacheManager) :
    CacheConfigManager<PvpZoneCacheManager>(plugin, "pvp.yml", cacheManager) {
    constructor(plugin: DeltaCraftTeams) : this(plugin, PvpZoneCacheManager(plugin))

    companion object {
        const val PvpZonesPrefix = "zones"
        const val PointOneKey = "pointOne"
        const val PointTwoKey = "pointTwo"
        const val TempKey = "temp"
    }


    override fun loadCache() {
        val regions = getZones()
        cacheManager.loadCache(regions)
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
        cacheManager.addItem(one, two, name)
    }

    fun removeZone(name: String) {
        val kh = KeyHelper(name, PvpZonesPrefix)
        config[kh.key] = null
        saveConfig()
        cacheManager.removeItem(name)
    }

    fun getTempPointOne(id: UUID): Location? {
        return getTempPoint(id, PointOneKey)
    }

    fun getTempPointTwo(id: UUID): Location? {
        return getTempPoint(id, PointTwoKey)
    }

    private fun getTempPoint(id: UUID, key: String): Location? {
        return getTempPoint(KeyHelper(id), key)
    }

    private fun getTempPoint(tempKeys: KeyHelper, key: String): Location? {
        val tempKey = tempKeys[TempKey, key]
        return cacheManager.getTempLocation(tempKey)
    }

    fun saveTempLocation(id: UUID, loc: Location, key: String) {
        val keys = KeyHelper(id)
        cacheManager.setTempLocation(keys[TempKey, key], loc)
    }
}