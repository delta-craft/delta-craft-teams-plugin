package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.TeamOwnerCache
import java.util.*

class TeamOwnerManager : CacheManager<UUID, TeamOwnerCache>() {

    override fun get(key: UUID): TeamOwnerCache? {
        val original = super.get(key) ?: return null
        if (original.isExpired) {
            remove(key)
            return null
        }
        return original
    }

    operator fun set(key: UUID, value: Boolean): TeamOwnerCache {
        return this.set(key, TeamOwnerCache(value))
    }

}