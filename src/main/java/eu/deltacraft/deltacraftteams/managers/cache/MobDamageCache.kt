package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import eu.deltacraft.deltacraftteams.types.PlayerEntityDamages
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

}