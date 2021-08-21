package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.SmithItemEvent


class SmithItemListener(
    private val pointsQueue: PointsQueue,
) : Listener {

    companion object {
        val upgradeables = hashMapOf<Material, Int>(
            Material.NETHERITE_SWORD to 15,
            Material.NETHERITE_HOE to 15,
            Material.NETHERITE_PICKAXE to 20,
            Material.NETHERITE_SHOVEL to 16,
            Material.NETHERITE_HELMET to 25,
            Material.NETHERITE_CHESTPLATE to 38,
            Material.NETHERITE_LEGGINGS to 35,
            Material.NETHERITE_BOOTS to 22,
        )
    }

    @EventHandler
    fun onSmithItem(event: SmithItemEvent) {
        if (event.whoClicked !is Player) return

        val player = event.whoClicked as Player

        /**
         * Slots
         * 0 - Left slot
         * 1 - Right slot
         * 2 - Result slot
         */

        val item = event.inventory.recipe?.result ?: event.inventory.getItem(2) ?: return

        // V tuhle chvíli, protože to našlo bodíky...
        // Ah! That's hot, that's hot!

        val type = item.type

        val points = upgradeables[type] ?: return

        val point = Point(points, player.uniqueId, PointType.Crafting, "Vyrobeno ${type.name}")
        point.addTag("Type", "Craft")
        point.addTag("Amount", 1)
        point.addTag("Item", type.name)

        pointsQueue.add(point)
    }
}