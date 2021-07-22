package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.PlayerEntityDamages
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

class MobDamageCache : CacheManager<UUID, PlayerEntityDamages>() {

    fun addDamage(entityUid: UUID, player: Player, damage: Double) {
        val playerUid = player.uniqueId

        val records = this[entityUid]

        val oldDamage = records[playerUid]

        records[playerUid] = oldDamage + damage

        this[entityUid] = records
    }

    override fun get(key: UUID): PlayerEntityDamages {
        val record = super.get(key)
        if (record != null) {
            return record
        }
        val toSave = PlayerEntityDamages()
        return this.set(key, toSave)
    }

    operator fun get(entityUid: UUID, playerUid: UUID): Double {
        return this[entityUid][playerUid]
    }

    fun getPoints(entity: Entity, maxPoints: Int): List<Point> {
        val entityUid = entity.uniqueId
        val records = this[entityUid]


        val points = mutableListOf<Point>()

        if (maxPoints < 1) return points

        if (records.isEmpty()) return points

        val damages = records.computePoints(maxPoints)

        if (damages.isEmpty()) return points

        val entityType = entity.type

        for (damage in damages) {
            val ratio = damage.value.toDouble() / maxPoints.toDouble()

            val point = Point(damage.value, damage.key, PointType.Warfare, "Zabití ${entity.type.name}")
            point.addTag("Type", "Mob")
            point.addTag("Entity", entityType.name)
            point.addTag("Participation", "%.4f".format(ratio))

            points.add(point)
        }

        return points
    }
}