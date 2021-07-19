package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.PointsError
import kotlinx.serialization.Serializable

@Serializable
data class PointsResult(val content: Boolean, val error: String? = null, val message: String? = null) {

    fun getErrorEnum(): PointsError? {
        if (error.isNullOrEmpty()) {
            return null
        }
        return PointsError.from(error)
    }

}
