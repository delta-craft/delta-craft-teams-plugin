package eu.deltacraft.deltacraftteams.types

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PvpZoneKillEvent(val killed: TeamPlayer, val killer: TeamPlayer, val pvpZone: PvpZone) : Event() {

    private val handlers = HandlerList()

    override fun getHandlers(): HandlerList {
        return handlers
    }

}