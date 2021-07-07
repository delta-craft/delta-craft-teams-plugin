package eu.deltacraft.deltacraftteams.managers.templates

import eu.deltacraft.deltacraftteams.DeltaCraftTeams


abstract class CacheConfigManager<T : CacheManager<*, *>>(
    plugin: DeltaCraftTeams,
    fileName: String,
    val cacheManager: T
) :
    ConfigManager(plugin, fileName) {

    override fun reloadAll() {
        clearCache()
        super.reloadAll()
        if (!cacheManager.needsLoad) {
            return
        }
        loadCache()
    }

    private fun clearCache() {
        cacheManager.clearCache()
    }

    abstract fun loadCache()

    init {
        if (cacheManager.needsLoad) {
            lazy { loadCache() }
        }
    }
}