package eu.deltacraft.deltacraftteams.types

import java.util.*

class PlayerEntityDamage private constructor(val playerUid: UUID, val damageDealt: Double) {
    constructor(playerUid: UUID) : this(playerUid, 0.0)

    fun addDamage(increment: Double): PlayerEntityDamage {
        return PlayerEntityDamage(playerUid, damageDealt + increment)
    }

}