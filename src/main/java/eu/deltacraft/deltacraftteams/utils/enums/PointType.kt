package eu.deltacraft.deltacraftteams.utils.enums

import kotlinx.serialization.Serializable

@Serializable
enum class PointType(val id: Int) {
    Mining(1),
    Crafting(2),
    Warfare(3),
    Journey(4);

    companion object {
        fun from(id: Int): PointType? = values().firstOrNull { it.id == id }
    }
}