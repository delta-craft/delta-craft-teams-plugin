package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.cache.PlayerHomeCache
import eu.deltacraft.deltacraftteams.types.hasMoved
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerHomeMoveListener(private val cache: PlayerHomeCache) : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!event.hasMoved()) {
            return
        }

        val player = event.player

        if (!cache.isTeleportPending(player)) {
            return
        }

        cache.cancelTeleport(player)

        val text = Component.text("Home teleportation was cancelled, because you moved!", NamedTextColor.RED)
        player.sendMessage(text)
    }
}