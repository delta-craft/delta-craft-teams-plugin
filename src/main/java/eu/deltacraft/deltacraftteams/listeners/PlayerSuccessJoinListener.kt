package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerSuccessJoinListener(private val teams: TeamCacheManager) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val team = teams[player] ?: return

        val text = Component.empty()
            .append(
                Component.text(team.name, team.majorTeamEnum.color)
            )
            .append(
                Component.text(" | ")
            )
            .append(player.displayName())

        player.displayName(text)
    }
}