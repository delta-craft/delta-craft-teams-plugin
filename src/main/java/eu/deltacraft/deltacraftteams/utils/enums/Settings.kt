package eu.deltacraft.deltacraftteams.utils.enums

enum class Settings(
    val path: String, // Cannot use getDescription because of JetBrains already using it somewhere
    val description: String,
    val visible: Boolean,
    private val type: String,
) {
    APIKEY(
        "system.apikey",
        "API key",
        false,
        "STRING"
    ),
    HOMEDELAY(
        "settings.home.delay",
        "Home teleport delay in seconds",
        true,
        "INT"
    ),
    MAXTEAMMARKERS(
        "settings.teammarkers.max",
        "Max team markers",
        true,
        "INT"
    );

    fun getType(): String {
        return "[$type]"
    }

    override fun toString(): String {
        return path
    }
}
