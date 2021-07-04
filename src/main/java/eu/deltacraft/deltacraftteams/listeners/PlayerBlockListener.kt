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

        val a = listOf<Pair<Material, Int>>(
            Pair(Material.DIAMOND_BLOCK, 1),
            Pair(Material.DIAMOND_ORE, 5)
        )

        if (a.none { x -> x.first == block.type })
            return

        val pair = a.first {x -> x.first == block.type}

        val ds = block.drops.size // TODO: FIX

        player.sendMessage("Destroyed ${block.type} value ${pair.second} ($ds DS)")

    }
}