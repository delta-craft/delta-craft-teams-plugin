package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.PlayerEntityDamage
import org.bukkit.entity.Player
import java.util.*

class MobDamageCache : CacheManager<UUID, PlayerEntityDamage>() {

    fun addDamage(entityUid: UUID, player: Player, damage: Double) {
        val record = this.getOrCreate(entityUid, player)

        val newRecord = record.addDamage(damage)

        this[entityUid] = newRecord
    }

    fun getOrCreate(entityUid: UUID, player: Player): PlayerEntityDamage {
        return getOrCreate(entityUid, player.uniqueId)
    }

    private fun getOrCreate(entityUid: UUID, playerId: UUID): PlayerEntityDamage {
        val record = this[entityUid]
        if (record != null) {
            return record
        }
        val toSave = PlayerEntityDamage(playerId)
        return this.set(entityUid, toSave)
    }

}