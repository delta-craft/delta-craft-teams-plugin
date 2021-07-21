package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.DeltaCraftTeamsManager
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneCacheManager
import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager
import eu.deltacraft.deltacraftteams.types.PvpZone
import eu.deltacraft.deltacraftteams.types.PvpZoneKillEvent
import eu.deltacraft.deltacraftteams.types.TeamPlayer
import org.bukkit.Bukkit
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

        val killedPlayerTeam = teamManager[killed] ?: return
        val killerPlayerTeam = teamManager[killer] ?: return

        // TODO: Check if same team → return
        if (killedPlayerTeam == killerPlayerTeam) {
            return
        }

        val killerLoc = killer.location
        if (!pvpManager.isInPvpZone(killerLoc)) return

        val loc = killed.location
        val zone = pvpManager[loc] ?: return
        // Kill in PVP area

        val killedTeamPlayer = TeamPlayer(killed, killedPlayerTeam)
        val killerTeamPlayer = TeamPlayer(killer, killerPlayerTeam)

        handlePvpZoneKill(killedTeamPlayer, killerTeamPlayer, zone)
    }

    private fun handlePvpZoneKill(
        killed: TeamPlayer,
        killer: TeamPlayer,
        pvpZone: PvpZone
    ) {
        val event = PvpZoneKillEvent(killed, killer, pvpZone)
        Bukkit.getPluginManager().callEvent(event)
    }
}