package eu.deltacraft.deltacraftteams.types

import java.util.UUID

data class UserConnection(val id: Int, val team_id: Int, var uuid: UUID?, val name: String)
