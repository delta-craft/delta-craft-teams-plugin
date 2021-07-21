package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.MajorTeam
import kotlinx.serialization.Serializable

@Serializable
data class Team(val id: Int, val name: String, val majorTeam: String) {

    fun getMajorTeamEnum(): MajorTeam {
        return MajorTeam.from(majorTeam, MajorTeam.Blue)
    }
}