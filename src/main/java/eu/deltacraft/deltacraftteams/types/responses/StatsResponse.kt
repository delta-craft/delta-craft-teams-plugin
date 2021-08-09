package eu.deltacraft.deltacraftteams.types.responses

import eu.deltacraft.deltacraftteams.types.StatsContent
import kotlinx.serialization.Serializable

@Serializable
data class StatsResponse(val content: StatsContent, val error: String? = null, val message: String? = null)
