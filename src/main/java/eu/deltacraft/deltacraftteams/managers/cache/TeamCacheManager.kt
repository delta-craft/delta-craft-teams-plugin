package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.Team
import org.bukkit.entity.Player
import java.util.*

class TeamCacheManager : CacheManager<UUID, Team>() {

    operator fun get(player: Player): Team? {
        return this[player.uniqueId]
    }

}