package eu.deltacraft.deltacraftteams.types.responses

import eu.deltacraft.deltacraftteams.types.Team
import kotlinx.serialization.Serializable

@Serializable
data class InnerSessionResponse(val success: Boolean, val team: Team? = null)
