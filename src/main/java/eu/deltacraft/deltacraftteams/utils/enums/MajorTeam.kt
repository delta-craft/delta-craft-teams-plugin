package eu.deltacraft.deltacraftteams.utils.enums

enum class MajorTeam(val value: String) {
    Blue("blue"),
    Red("red");

    companion object {
        fun from(findValue: String): MajorTeam? = values().firstOrNull { it.value == findValue }

        fun from(findValue: String, default: MajorTeam): MajorTeam =
            values().firstOrNull { it.value == findValue } ?: default
    }
}