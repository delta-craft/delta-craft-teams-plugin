package eu.deltacraft.deltacraftteams.types

import java.util.*

class PlayerEntityDamages : HashMap<UUID, Double>() {

    override fun get(playerUid: UUID): Double {
        val damage = super.get(playerUid) ?: 0.0
        this[playerUid] = damage
        return damage
    }
}