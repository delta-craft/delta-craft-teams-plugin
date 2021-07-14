package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.TeleportBar
import org.bukkit.entity.Player
import java.util.UUID

class PlayerHomeCache : CacheManager<UUID, TeleportBar>(false) {

    fun isTeleportPending(player: Player): Boolean {
        return isTeleportPending(player.uniqueId)
    }

    fun isTeleportPending(uid: UUID): Boolean {
        return containsKey(uid)
    }

    fun cancelTeleport(player: Player) {
        cancelTeleport(player.uniqueId)
    }

    private fun cancelTeleport(uid: UUID) {
        get(uid)?.hideBar()
        remove(uid)
    }
}