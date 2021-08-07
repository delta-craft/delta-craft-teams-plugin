package eu.deltacraft.deltacraftteams.types.responses

import kotlinx.serialization.Serializable

@Serializable
data class CheckChatResponse(val content: Boolean, val message: String? = null)
