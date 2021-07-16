package eu.deltacraft.deltacraftteams.types

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PointTag constructor(val key: String, val value: String, @Transient val point: Point? = null) {
    constructor(key: String, value: String) : this(key, value, null)


}