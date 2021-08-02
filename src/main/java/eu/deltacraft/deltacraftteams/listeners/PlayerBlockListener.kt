package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.DestroyedBlock
import eu.deltacraft.deltacraftteams.types.points.MiningPoint
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent

class PlayerBlockListener(private val pointsQueue: PointsQueue) : Listener {

    companion object {
        val list = listOf(
            // WOODEN PICKAXE
            DestroyedBlock(Material.COAL_ORE, 1, Material.WOODEN_PICKAXE),
            DestroyedBlock(Material.NETHER_GOLD_ORE, 3, Material.WOODEN_PICKAXE),
            DestroyedBlock(Material.NETHER_QUARTZ_ORE, 4, Material.WOODEN_PICKAXE),
            DestroyedBlock(Material.SPAWNER, 15, Material.WOODEN_PICKAXE),

            // STONE PICKAXE
            DestroyedBlock(Material.COPPER_ORE, 5, Material.STONE_PICKAXE),
            DestroyedBlock(Material.LAPIS_ORE, 7, Material.STONE_PICKAXE),
            DestroyedBlock(Material.IRON_ORE, 11, Material.STONE_PICKAXE),

            DestroyedBlock(Material.DEEPSLATE_COPPER_ORE, 5, Material.STONE_PICKAXE),
            DestroyedBlock(Material.DEEPSLATE_LAPIS_ORE, 7, Material.STONE_PICKAXE),
            DestroyedBlock(Material.DEEPSLATE_IRON_ORE, 11, Material.STONE_PICKAXE),

            // IRON PICKAXE
            DestroyedBlock(Material.GOLD_ORE, 11, Material.IRON_PICKAXE),
            DestroyedBlock(Material.REDSTONE_ORE, 11, Material.IRON_PICKAXE),
            DestroyedBlock(Material.DIAMOND_ORE, 15, Material.IRON_PICKAXE),
            DestroyedBlock(Material.EMERALD_ORE, 30, Material.IRON_PICKAXE),

            DestroyedBlock(Material.DEEPSLATE_GOLD_ORE, 11, Material.IRON_PICKAXE),
            DestroyedBlock(Material.DEEPSLATE_REDSTONE_ORE, 11, Material.IRON_PICKAXE),
            DestroyedBlock(Material.DEEPSLATE_DIAMOND_ORE, 15, Material.IRON_PICKAXE),
            DestroyedBlock(Material.DEEPSLATE_EMERALD_ORE, 30, Material.IRON_PICKAXE),

            // DIAMOND PICKAXE
            DestroyedBlock(Material.ANCIENT_DEBRIS, 40, Material.DIAMOND_PICKAXE),

            // NETHERITE PICKAXE
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
        if (!picks.contains(current)) return false

        return picks.indexOf(current) >= picks.indexOf(required)
    }

    @EventHandler
    fun onBlockDrop(event: BlockDropItemEvent) {
        val player = event.player

        if (player.gameMode != GameMode.SURVIVAL) return

        val tool = player.inventory.itemInMainHand

        if (tool.type == Material.AIR) return

        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) return

        val material = event.blockState.type

        val db = list.singleOrNull { it.material == material && it.minTool == tool.type }
            ?: list.lastOrNull { it.material == material }
            ?: return

        if (!pickaxeMeetsCriteria(tool.type, db.minTool)) return

        // Player met criteria to receive points
        val dropSize = if (event.items.any()) event.items.first().itemStack.amount else 1

        val point = MiningPoint(db.points, player.uniqueId, material.name, tool.type.name, dropSize)
        point.drops.add(dropSize)

        pointsQueue.add(point)
    }
}