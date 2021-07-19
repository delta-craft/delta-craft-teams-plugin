package eu.deltacraft.deltacraftteams.types

import kotlinx.serialization.Serializable

@Serializable
data class PointTag constructor(val key: String, val value: String) {

}