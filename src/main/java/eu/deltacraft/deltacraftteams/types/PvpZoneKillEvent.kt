package eu.deltacraft.deltacraftteams.types

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PvpZoneKillEvent(val killed: TeamPlayer, val killer: TeamPlayer, val pvpZone: PvpZone) : Event() {

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {

        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

}