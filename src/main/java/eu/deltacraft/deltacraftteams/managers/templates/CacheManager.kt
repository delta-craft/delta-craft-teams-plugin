package eu.deltacraft.deltacraftteams.managers.templates

abstract class CacheManager<K, V>(val needsLoad: Boolean) : MutableMap<K, V> {

    private val cache: HashMap<K, V> = HashMap()
    private var isLoaded: Boolean = false

    override fun put(key: K, value: V): V? {
        checkLoad()
        cache[key] = value
        return value
    }

    override fun remove(key: K): V? {
        checkLoad()
        return cache.remove(key)
    }

    override val values: MutableCollection<V>
        get() {
            checkLoad()
            return cache.values
        }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            checkLoad()
            return cache.entries
        }

    override val keys: MutableSet<K>
        get() {
            checkLoad()
            return cache.keys
        }

    override operator fun get(key: K): V? {
        checkLoad()
        return cache[key]
    }

    override val size: Int
        get() {
            checkLoad()
            return cache.size
        }

    override fun containsKey(key: K): Boolean {
        checkLoad()
        return cache.containsKey(key)
    }

    override fun containsValue(value: V): Boolean {
        checkLoad()
        return cache.containsValue(value)
    }

    override fun putAll(from: Map<out K, V>) {
        if (!needsLoad) {
//            throw new Exception("This manager does not need loading!");
            return
        }
        if (isLoaded) {
//            throw new Exception("This manager is already loaded");
            return
        }
        cache.clear()
        cache.putAll(from)
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

    override fun clear() {
        checkLoad()
        cache.clear()
    }

    override fun isEmpty(): Boolean {
        checkLoad()
        return cache.isEmpty()
    }

}