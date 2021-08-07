package eu.deltacraft.deltacraftteams.utils.enums

enum class PointsError(val value: String) {
    NoPlayer("no_players"),
    Unknown("unknown");

    companion object {
        fun from(findValue: String): PointsError? = values().firstOrNull { it.value == findValue }
    }
}