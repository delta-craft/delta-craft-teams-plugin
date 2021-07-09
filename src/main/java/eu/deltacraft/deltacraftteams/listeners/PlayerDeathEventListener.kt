package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.DeltaCraftTeamsManager
import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneCacheManager
import eu.deltacraft.deltacraftteams.types.PvpZone
import eu.deltacraft.deltacraftteams.types.PvpZoneKillEvent
import eu.deltacraft.deltacraftteams.types.TeamPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerDeathEventListener(
    private val pvpManager: PvpZoneCacheManager,
    private val teamManager: TeamCacheManager,
) : Listener {
    constructor(manager: DeltaCraftTeamsManager) : this(manager.pvpZoneCacheManager, manager.teamCacheManager)

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val killed = event.entity
        val killer = killed.killer ?: return

        val killedPlayerTeam = teamManager[killed.uniqueId] ?: return
        val killerPlayerTeam = teamManager[killer.uniqueId] ?: return

        // TODO: if same team → return
        if (killedPlayerTeam == killerPlayerTeam) {
            return
        }

        val loc = killed.location
        val zone = pvpManager[loc]
        if (zone == null) {
            hadleNonPvpZoneKill(killed, killer)
            return
        }
        // Kill in PVP area

        val killedTeamPlayer = TeamPlayer(killed, killedPlayerTeam)
        val killerTeamPlayer = TeamPlayer(killer, killerPlayerTeam)

        handlePvpZoneKill(killedTeamPlayer, killerTeamPlayer, zone)
    }

    private fun hadleNonPvpZoneKill(killed: Player, killer: Player) {
        // TODO: Implement
    }

    private fun handlePvpZoneKill(killed: TeamPlayer, killer: TeamPlayer, pvpZone: PvpZone) {
        // TODO: Implement


        val event = PvpZoneKillEvent(killed, killer, pvpZone)
        Bukkit.getPluginManager().callEvent(event)
    }
}