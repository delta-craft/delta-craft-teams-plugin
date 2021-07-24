package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.cache.*

class DeltaCraftTeamsManager(private val plugin: DeltaCraftTeams) {

    val pvpZoneCacheManager = PvpZoneCacheManager()
    val teamCacheManager = TeamCacheManager()
    val loginCacheManager = LoginCacheManager(teamCacheManager)
    val zoneEnterCache = PvpZoneEnterCache()
    val mobDamageCache = MobDamageCache()
    val teamOwnerManager = TeamOwnerManager()

}
