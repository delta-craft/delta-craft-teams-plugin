package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class PlayerBlockListener(private val plugin: DeltaCraftTeams) : Listener {

    @EventHandler
    fun onBlockBreak(blockBreakEvent: BlockBreakEvent) {
        val player = blockBreakEvent.player
        val block = blockBreakEvent.block

        player.sendMessage("Destroyed ${block.type}")
    }


}