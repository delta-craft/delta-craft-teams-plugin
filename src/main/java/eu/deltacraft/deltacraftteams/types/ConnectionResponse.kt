package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.ValidateError

data class ConnectionResponse(val content: Boolean, val error: String) {

    fun getErrorEnum(): ValidateError? {
        return ValidateError.from(error)
    }


}