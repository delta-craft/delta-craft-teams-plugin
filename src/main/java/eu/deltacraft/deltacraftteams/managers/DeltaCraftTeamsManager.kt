package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.cache.PlayerHomeCache
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneCacheManager
import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager

class DeltaCraftTeamsManager(private val plugin: DeltaCraftTeams) {

    val pvpZoneCacheManager = PvpZoneCacheManager()
    val teamCacheManager = TeamCacheManager()

}
