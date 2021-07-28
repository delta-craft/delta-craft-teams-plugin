package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.managers.cache.MobDamageCache
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class MobDamageListener(
    private val mobDamageCache: MobDamageCache,
    private val pointsQueue: PointsQueue,
) : Listener {

    companion object {
        val map = hashMapOf(
            EntityType.ENDER_DRAGON to 1000,
            EntityType.WITHER to 1000,
            EntityType.GHAST to 50,
            EntityType.ENDERMAN to 10,
            EntityType.WITHER_SKELETON to 5,
            EntityType.PHANTOM to 15,
            EntityType.RAVAGER to 30,
            EntityType.EVOKER to 5,
            EntityType.VINDICATOR to 5,
            EntityType.PILLAGER to 5,
            EntityType.WITCH to 5,
            EntityType.ELDER_GUARDIAN to 50,
            EntityType.SHULKER to 10,
        )
    }

    @EventHandler(ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (!map.containsKey(event.entityType)) return

        val damager = event.damager
        if (damager !is Player) return

        val entity = event.entity
        val entityUid = entity.uniqueId
        val player: Player = damager
        val damage = event.damage

        mobDamageCache.addDamage(entityUid, player, damage)
    }

    @EventHandler(ignoreCancelled = true)
    fun onKill(event: EntityDeathEvent) {
        if (!map.containsKey(event.entityType)) return

        val entity = event.entity

        val maxPoints = map[event.entityType] ?: 0

        val points = mobDamageCache.getPoints(entity, maxPoints)

        if (points.isEmpty()) return

        pointsQueue.add(points)
    }
}