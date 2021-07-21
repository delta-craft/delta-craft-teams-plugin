package eu.deltacraft.deltacraftteams.types

import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

class PvpZoneKillEvent(val killed: TeamPlayer, val killer: TeamPlayer, val pvpZone: PvpZone) : Event() {

    val weapon: ItemStack? = killer.player.itemInUse

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