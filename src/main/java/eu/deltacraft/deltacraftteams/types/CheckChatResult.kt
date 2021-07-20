package eu.deltacraft.deltacraftteams.types

import kotlinx.serialization.Serializable

@Serializable
data class CheckChatResult(val content: Boolean, val message: String? = null)
