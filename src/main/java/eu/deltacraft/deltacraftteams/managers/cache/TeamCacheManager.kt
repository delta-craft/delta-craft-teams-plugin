package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.Team
import java.util.UUID

class TeamCacheManager : CacheManager<UUID, Team>(false) {
}