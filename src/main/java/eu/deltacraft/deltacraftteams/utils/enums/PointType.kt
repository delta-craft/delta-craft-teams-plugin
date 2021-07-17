package eu.deltacraft.deltacraftteams.utils.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PointType(val id: Int) {
    @SerialName("1")
    Mining(1),

    @SerialName("2")
    Crafting(2),

    @SerialName("3")
    Warfare(3),

    @SerialName("4")
    Journey(4);
}