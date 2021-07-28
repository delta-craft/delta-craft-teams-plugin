package eu.deltacraft.deltacraftteams.types

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.util.*

data class TeamMarker(
    val id: UUID,
    val name: String,
    val teamId: Int,
    val location: Location,
) : ConfigurationSerializable {
    constructor(name: String, teamId: Int, location: Location) :
            this(UUID.randomUUID(), name, teamId, location)

    override fun serialize(): MutableMap<String, Any> {
        val res = mutableMapOf<String, Any>()

        res["id"] = id.toString()
        res["name"] = name
        res["teamId"] = teamId
        res["location"] = location.serialize()

        return res
    }

    fun getInfo(): TextComponent {
        return Component.empty()
            .append(
                Component.text(name)
                    .hoverEvent(
                        HoverEvent.showText(
                            Component.text("ID: $id")
                        )
                    )
            ).append(
                Component.text(" [DELETE] ", NamedTextColor.RED)
                    .clickEvent(
                        ClickEvent.suggestCommand("/teammarker remove $id")
                    )
                    .hoverEvent(
                        HoverEvent.showText(
                            Component.text("Click to suggest command")
                        )
                    )
            )
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

            return TeamMarker(id, name, teamId, location)
        }
    }


}