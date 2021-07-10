package eu.deltacraft.deltacraftteams.managers.templates

import eu.deltacraft.deltacraftteams.DeltaCraftTeams


abstract class CacheConfigManager<T : CacheManager<*, *>>(
    plugin: DeltaCraftTeams,
    fileName: String,
    val cacheManager: T
) :
    ConfigManager(plugin, fileName) {

    init {
        if (cacheManager.needsLoad) {
            loadCache()
        }
    }

    fun reload() {
        clearCache()
        reloadAll()

        if (!cacheManager.needsLoad) {
            return
        }
        loadCache()
    }

    private fun clearCache() {
        cacheManager.clear()
    }

    abstract fun loadCache()
}