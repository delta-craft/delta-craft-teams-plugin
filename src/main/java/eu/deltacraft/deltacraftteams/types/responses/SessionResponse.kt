package eu.deltacraft.deltacraftteams.types.responses

import eu.deltacraft.deltacraftteams.utils.enums.SessionError
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(val content: InnerSessionResponse, val error: String? = null) {

    fun getErrorEnum(): SessionError? {
        if (error.isNullOrEmpty()) {
            return null
        }
        return SessionError.from(error)
    }
}
