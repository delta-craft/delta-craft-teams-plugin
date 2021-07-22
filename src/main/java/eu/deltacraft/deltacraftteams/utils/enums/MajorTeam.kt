package eu.deltacraft.deltacraftteams.utils.enums

import net.kyori.adventure.text.format.NamedTextColor

enum class MajorTeam(val value: String, val color: NamedTextColor) {
    Blue("blue", NamedTextColor.BLUE),
    Red("red", NamedTextColor.RED),
    None("none", NamedTextColor.GRAY);

    companion object {
        fun from(findValue: String): MajorTeam? = values().firstOrNull { it.value == findValue }

        fun from(findValue: String?, default: MajorTeam): MajorTeam {
            if (findValue == null) {
                return default
            }
            return values().firstOrNull { it.value == findValue } ?: default
        }
    }
}