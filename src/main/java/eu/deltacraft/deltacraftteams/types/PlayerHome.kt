package eu.deltacraft.deltacraftteams.types

import org.bukkit.Location
import java.util.UUID

data class PlayerHome(var playerId: UUID, val location: Location) {
}