package eu.deltacraft.deltacraftteams.types

import kotlinx.serialization.Serializable

@Serializable
data class NewLoginData(val uuid: String, val ip: String) {

}