package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.PvpZone
import org.bukkit.entity.Player
import java.util.*

class PvpZoneEnterCache : CacheManager<UUID, String>() {

    fun remove(p: Player): String? {
        return this.remove(p.uniqueId)
    }

    operator fun set(key: UUID, value: PvpZone): String {
        return this.set(key, value.name)
    }
}