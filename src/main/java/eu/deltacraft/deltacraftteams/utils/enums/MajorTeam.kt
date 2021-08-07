package eu.deltacraft.deltacraftteams.utils.enums

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor

enum class MajorTeam(val value: String, val color: TextColor) {
    Blue("blue", TextColor.color(0x0067c4)),
    Red("red", TextColor.color(0xbd1b1b)),
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