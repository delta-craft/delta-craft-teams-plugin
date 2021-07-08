package eu.deltacraft.deltacraftteams.managers.templates

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import java.lang.Exception

abstract class CacheManager<TKey, T>(protected val plugin: DeltaCraftTeams, val needsLoad: Boolean) {

    private val cache: HashMap<TKey, T> = HashMap()
    private var isLoaded: Boolean = false

    fun addItem(id: TKey, item: T) {
        cache[id] = item
    }

    fun removeItem(id: TKey) {
        cache.remove(id)
    }

    fun getCache(): HashMap<TKey, T> {
        checkLoad()
        return cache
    }

    val values: Collection<T>
        get() = cache.values

    operator fun get(key: TKey): T? {
        checkLoad()
        return cache[key]
    }

    val count: Int
        get() = cache.size

    operator fun contains(key: TKey): Boolean {
        return cache.containsKey(key)
    }

    fun loadCache(toLoad: HashMap<TKey, T>) {
        if (!needsLoad) {
//            throw new Exception("This manager does not need loading!");
            return
        }
        if (isLoaded) {
//            throw new Exception("This manager is already loaded");
            return
        }
        cache.clear()
        cache.putAll(toLoad)
        isLoaded = true
    }

    private fun checkLoad() {
        if (!needsLoad) {
            return
        }
        if (!isLoaded) {
            throw Exception("Manager is not loaded")
        }
    }

    fun clearCache() {
        cache.clear()
    }
}