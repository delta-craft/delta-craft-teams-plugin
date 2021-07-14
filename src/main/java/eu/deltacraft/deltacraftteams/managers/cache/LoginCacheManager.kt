package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import java.util.*

class LoginCacheManager : CacheManager<UUID, Boolean>(false) {
    fun loginPlayer(playerUuid: UUID)  {
        this[playerUuid] = true
    }

    fun logoutPlayer(playerUuid: UUID) {
        this[playerUuid] = false
    }

    fun isLoggedIn(playerUuid: UUID): Boolean {
        if (!this.containsKey(playerUuid)) return false
        return this[playerUuid]!!
    }
}