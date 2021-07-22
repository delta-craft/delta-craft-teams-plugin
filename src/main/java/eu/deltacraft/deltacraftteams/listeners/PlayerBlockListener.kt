package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.DestroyedBlock
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class PlayerBlockListener(private val pointsQueue: PointsQueue) : Listener {

    companion object {
        val list = listOf(
            // WOODEN PICKAXE
            DestroyedBlock(Material.COAL_ORE, 1, Material.WOODEN_PICKAXE),
            DestroyedBlock(Material.GOLD_ORE, 3, Material.WOODEN_PICKAXE),
            // STONE PICKAXE
            DestroyedBlock(Material.COPPER_ORE, 5, Material.STONE_PICKAXE),
            DestroyedBlock(Material.LAPIS_ORE, 7, Material.STONE_PICKAXE),
            DestroyedBlock(Material.IRON_ORE, 11, Material.STONE_PICKAXE),
            DestroyedBlock(Material.GOLD_ORE, 3, Material.WOODEN_PICKAXE),
            // IRON PICKAXE
            DestroyedBlock(Material.GOLD_ORE, 11, Material.IRON_PICKAXE),
            DestroyedBlock(Material.REDSTONE_ORE, 11, Material.IRON_PICKAXE),
            DestroyedBlock(Material.DIAMOND_ORE, 15, Material.IRON_PICKAXE),
            DestroyedBlock(Material.EMERALD_ORE, 30, Material.IRON_PICKAXE),
            // DIAMOND PICKAXE
            DestroyedBlock(Material.ANCIENT_DEBRIS, 40, Material.DIAMOND_PICKAXE),
            DestroyedBlock(Material.GOLD_ORE, 11, Material.IRON_PICKAXE),
        )

        val picks = listOf(
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE,
        )
    }

    private fun pickaxeMeetsCriteria(current: Material, required: Material): Boolean {
        if (picks.indexOf(current) < 0) return false

        return picks.indexOf(current) >= picks.indexOf(required)
    }

    @EventHandler
    fun onBlockDrop(event: BlockDropItemEvent) {
        val player = event.player
        val playerId = player.uniqueId
        val droppedItems = event.items
        val material = event.blockState.type

        if (player.gameMode != GameMode.SURVIVAL) return

        val tool = player.inventory.itemInMainHand

        if (tool.type == Material.AIR) return

        val db = list.firstOrNull { it.material == material && it.minTool == tool.type }
            ?: list.firstOrNull { it.material == material }
            ?: return

        if (!pickaxeMeetsCriteria(tool.type, db.minTool)) return

        // Player met criteria to receive points

        val point = Point(db.points, playerId, PointType.Mining, "Vykopán blok: ${material.name}")

        point.addTag("Block", material.name)
        point.addTag("DropSize", droppedItems.size)
        point.addTag("Tool", tool.type.name)

        pointsQueue.add(point)
    }
}