package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class PlayerBlockListener(private val plugin: DeltaCraftTeams) : Listener {

    @EventHandler
    fun onBlockDrop(event: BlockDropItemEvent) {
        val player = event.player
        var playerId = player.uniqueId
        val items = event.items
        val material = event.blockState.type

        val a = listOf(
            Pair(Material.DIAMOND_BLOCK, 1),
            Pair(Material.DIAMOND_ORE, 5),
            Pair(Material.DEEPSLATE_DIAMOND_ORE, 5)
        )

        if (a.none { x -> x.first == material })
            return

        val pair = a.firstOrNull { x -> x.first == material }

        val item = items.firstOrNull()?.itemStack

        val amount = item?.amount ?: 0

        player.sendMessage("Destroyed $material value ${pair?.second} ($amount DS)")

    }
}