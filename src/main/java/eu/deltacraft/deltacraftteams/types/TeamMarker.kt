package eu.deltacraft.deltacraftteams.types

import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.*

data class TeamMarker(
    val id: UUID,
    val name: String,
    val teamId: Int,
    val location: Location,
    val description: String = "",
) : ConfigurationSerializable {
    constructor(name: String, teamId: Int, location: Location, description: String = "") :
            this(UUID.randomUUID(), name, teamId, location, description)


    fun setDescription(newDescription: String): TeamMarker {
        return TeamMarker(id, name, teamId, location, newDescription)
    }

    override fun serialize(): MutableMap<String, Any> {
        val res = mutableMapOf<String, Any>()

        res["id"] = id.toString()
        res["name"] = name
        res["teamId"] = teamId.toString()
        res["location"] = location.serialize()
        res["description"] = description

        return res
    }

    companion object {
        @JvmStatic
        fun deserialize(data: MutableMap<String, Any?>): TeamMarker? {
            if (data.isEmpty()) {
                return null
            }
            val idString = data["id"]?.toString()
            if (idString.isNullOrEmpty()) {
                return null
            }
            val id = UUID.fromString(idString)
            val name = data["name"]?.toString() ?: return null
            val teamIdString = data["teamId"]?.toString()
            if (teamIdString.isNullOrEmpty()) {
                return null
            }
            val teamId = teamIdString.toIntOrNull() ?: return null
            val locAny = data["location"] ?: return null
            if (locAny !is Map<*, *>) {
                return null
            }
            @Suppress("UNCHECKED_CAST")
            val locMap = locAny as? Map<String, Any?>? ?: return null

            val location = Location.deserialize(locMap)

            val description = data["description"]?.toString() ?: ""

            return TeamMarker(id, name, teamId, location, description)
        }
    }


}