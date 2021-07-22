package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.cache.MobDamageCache
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class MobDamageListener(private val mobDamageCache: MobDamageCache) : Listener {

    companion object {
        val list = hashMapOf(
            EntityType.ENDER_DRAGON to 1000,
            EntityType.WITHER to 1000
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!list.containsKey(event.entityType)) return

        val damager = event.damager
        if (damager !is Player) return

        val entity = event.entity
        val entityUid = entity.uniqueId
        val player: Player = damager
        val damage = event.damage

        mobDamageCache.addDamage(entityUid, player, damage)
    }

}