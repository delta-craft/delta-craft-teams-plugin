package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.cache.LoginCacheManager
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneCacheManager
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneEnterCache
import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager

class DeltaCraftTeamsManager(private val plugin: DeltaCraftTeams) {

    val pvpZoneCacheManager = PvpZoneCacheManager()
    val teamCacheManager = TeamCacheManager()
    val loginCacheManager = LoginCacheManager(teamCacheManager)
    val zoneEnterCache = PvpZoneEnterCache()

}
