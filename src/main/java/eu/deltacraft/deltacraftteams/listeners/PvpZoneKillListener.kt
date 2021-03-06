package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.PvpZoneKillEvent
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PvpZoneKillListener(private val pointsQueue: PointsQueue) : Listener {

    @EventHandler
    fun onPvpZoneKill(event: PvpZoneKillEvent) {
        val killed = event.killed
        val killer = event.killer
        val zone = event.pvpZone
        val loc = event.location

        val killedPlayer = killed.player
        val killedTeam = killed.killedPlayerTeam
        val killerPlayer = killer.player
        val killerTeam = killer.killedPlayerTeam
        val weapon = event.weapon?.type?.name ?: "Hand"

        val description =
            "Zabit ${killedPlayer.name} (${killedTeam.majorTeam} Tým: ${killedTeam.name})"

        val point = Point(300, killerPlayer.uniqueId, PointType.Warfare, description)
        point.addTag("Type", "PVP")
        point.addTag("Zone", zone.name)
        point.addTag("Killer", killerPlayer.name)
        point.addTag("Killed", killedPlayer.name)
        point.addTag("KillerTeamId", killerTeam.id)
        point.addTag("KilledTeamId", killedTeam.id)
        point.addTag("Weapon", weapon)
        point.addTag(loc)

        pointsQueue.add(point)
    }

}