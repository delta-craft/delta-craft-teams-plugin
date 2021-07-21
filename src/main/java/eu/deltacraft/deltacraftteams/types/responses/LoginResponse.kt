package eu.deltacraft.deltacraftteams.types.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val content: Boolean, val error: String? = null)
