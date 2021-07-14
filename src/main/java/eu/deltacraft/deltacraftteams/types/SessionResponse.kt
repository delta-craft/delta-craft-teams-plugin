package eu.deltacraft.deltacraftteams.types
import eu.deltacraft.deltacraftteams.utils.enums.SessionError

data class SessionResponse(val content: Boolean, val error: String) {
    fun getErrorEnum(): SessionError? {
        return SessionError.from(error)
    }
}
