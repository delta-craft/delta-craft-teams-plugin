package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.MajorTeam
import kotlinx.serialization.Serializable

@Serializable
data class Team(val id: Int, val name: String, val majorTeam: String?) : Comparable<Any> {

    val majorTeamEnum: MajorTeam
        get() = MajorTeam.from(majorTeam ?: "none", MajorTeam.None)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (other !is Team) return false

        return other.id == this.id
    }

    override fun hashCode(): Int = id

    override fun compareTo(other: Any): Int = if (equals(other)) 0 else -1

}