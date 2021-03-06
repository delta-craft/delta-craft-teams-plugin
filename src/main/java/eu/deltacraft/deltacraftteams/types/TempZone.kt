package eu.deltacraft.deltacraftteams.types

import org.bukkit.Location

data class TempZone(var first: Location? = null, var second: Location? = null) {

    val isSet: Boolean
        get() {
            return first != null && second != null
        }
}