package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.ValidateError
import kotlinx.serialization.Serializable

@Serializable
data class ConnectionResponse(val content: Boolean, val error: String? = null) {

    fun getErrorEnum(): ValidateError? {
        if (error.isNullOrEmpty()) {
            return null
        }
        return ValidateError.from(error)
    }
}