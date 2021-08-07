package eu.deltacraft.deltacraftteams.listeners

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class ShulkerKillListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onShulkerKill(e: EntityDeathEvent) {
        if (e.entityType != EntityType.SHULKER) return

        val toDrop = ItemStack(Material.SHULKER_SHELL, 2)
        e.drops.clear()
        e.drops.add(toDrop)
    }
}