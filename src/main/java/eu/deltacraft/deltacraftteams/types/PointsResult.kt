package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.PointsError

data class PointsResult(val content: Boolean, val error: String, val message: String? = null) {

    fun getErrorEnum(): PointsError? {
        return PointsError.from(error)
    }

}
