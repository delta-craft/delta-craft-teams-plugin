package eu.deltacraft.deltacraftteams.types

import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt

class PlayerEntityDamages : HashMap<UUID, Double>() {

    override fun get(key: UUID): Double {
        val damage = super.get(key) ?: 0.0
        this[key] = damage
        return damage
    }

    fun computePoints(maxPoints: Int): HashMap<UUID, Int> {

        val ratios = computeRatio()

        val pointsMap = hashMapOf<UUID, Int>()

        for (entry in ratios) {
            val points = floor(maxPoints * entry.value).roundToInt()
            if (points < 1) {
                continue
            }
            pointsMap[entry.key] = points
        }

        return pointsMap
    }

    private fun computeRatio(): HashMap<UUID, Double> {
        val sum = this.values.sum()

        val ratios = hashMapOf<UUID, Double>()

        for (enty in this) {
            ratios[enty.key] = enty.value / sum
        }

        return ratios
    }

}