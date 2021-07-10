package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager

class TempCache<K, V> : CacheManager<K, V>(false) {

    fun getOrCreate(key: K, init: () -> V): V {
        if (containsKey(key)) {
            return this[key]!!
        }
        return init()
    }
}