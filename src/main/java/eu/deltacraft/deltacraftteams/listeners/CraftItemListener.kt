package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class CraftItemListener(private val pointsQueue: PointsQueue) : Listener {

    companion object {
        val map = hashMapOf<Material, Int>(
            // BLOCKS
            // -------
            Material.BEACON to 35,
            Material.ENCHANTING_TABLE to 10,
            Material.END_CRYSTAL to 20,
            Material.ENDER_CHEST to 20,
            // -------
            // ITEMS
            // -------
            Material.GOLDEN_APPLE to 10,
            // -------
            // TOOLS
            // -------
            // Swords
            Material.STONE_SWORD to 2,
            Material.GOLDEN_SWORD to 4,
            Material.IRON_SWORD to 4,
            Material.DIAMOND_SWORD to 15,
            // Hoes
            Material.DIAMOND_HOE to 15,
            // Pickaxes
            Material.DIAMOND_PICKAXE to 15,
            // -------
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun onCraftItem(event: CraftItemEvent) {
        val resultItem = event.recipe.result
        val type = resultItem.type

        val points = map[type] ?: return
        val whoClicked = event.whoClicked
        if (whoClicked !is Player) return

        val player: Player = whoClicked

        val amount = getRealAmount(event.click, event.inventory, resultItem)

        val realPoints = points * amount

        val point = Point(realPoints, player.uniqueId, PointType.Crafting, "Vyrobeno ${amount}× ${type.name}")
        point.addTag("Type", "Craft")
        point.addTag("Amount", amount)
        point.addTag("Item", type.name)

        pointsQueue.add(point)
    }

    private fun getRealAmount(clickType: ClickType, inventory: Inventory, craftedItem: ItemStack): Int {
        // https://www.spigotmc.org/threads/how-to-get-amount-of-item-crafted.377598/

        if (!clickType.isShiftClick) return 1

        // Is shift click

        // Set lower at recipe result max stack size + 1000 (or just higher max stack size of recipe item)
        var lowerAmount = craftedItem.maxStackSize + 1000

        // Info: Nemám tušení, jak to funguje, ale funguje to :D

        for (actualItem in inventory.contents) {
            //if slot is not air &&
            // lowerAmount is higher than this slot amount &&
            // it's not the recipe amount
            if (!actualItem.type.isAir &&
                lowerAmount > actualItem.amount &&
                actualItem.type != craftedItem.type
            ) {
                lowerAmount = actualItem.amount
            }
        }

        return lowerAmount * craftedItem.amount
    }
}