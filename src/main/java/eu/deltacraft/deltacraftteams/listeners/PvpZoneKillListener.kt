package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.types.PvpZoneKillEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PvpZoneKillListener : Listener {

    @EventHandler
    fun onPvpZoneKill(event: PvpZoneKillEvent) {
        val killed = event.killed
        val killer = event.killer

    }

}