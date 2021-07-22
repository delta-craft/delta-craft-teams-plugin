package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.Team
import java.util.*

class LoginCacheManager(private val teamsCacheManager: TeamCacheManager) : CacheManager<UUID, Boolean>() {
    fun loginPlayer(playerUuid: UUID, team: Team) {
        teamsCacheManager[playerUuid] = team
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