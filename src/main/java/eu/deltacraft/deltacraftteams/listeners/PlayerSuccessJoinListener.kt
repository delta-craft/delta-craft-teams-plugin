package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.DeltaCraftTeamsManager
import eu.deltacraft.deltacraftteams.managers.ScoreboardIntegrations
import eu.deltacraft.deltacraftteams.managers.cache.JoinTimeCache
import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager
import eu.deltacraft.deltacraftteams.types.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerSuccessJoinListener(private val teams: TeamCacheManager, private val joinCache: JoinTimeCache) : Listener {
    constructor(manager: DeltaCraftTeamsManager) : this(manager.teamCacheManager, manager.joinTimeCache)

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val team = teams[player] ?: return

        val url = "${Constants.FULL_URL}/teams/${team.id}"

        val text = Component.empty()
            .append(
                Component.text(team.name, team.majorTeamEnum.color, TextDecoration.BOLD)
                    .clickEvent(
                        ClickEvent.openUrl(url)
                    )
                    .hoverEvent(
                        HoverEvent.showText(
                            Component.text("Zobrazit statistiku týmu")
                        )
                    )
            )
            .append(
                Component.text(" | ")
            )
            .append(player.displayName())

        player.displayName(text)

        joinCache.playerJoined(player.uniqueId)

        ScoreboardIntegrations.registerPlayer(player, team)
    }
}