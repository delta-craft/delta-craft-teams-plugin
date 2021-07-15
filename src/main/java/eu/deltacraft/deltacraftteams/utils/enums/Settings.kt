package eu.deltacraft.deltacraftteams.utils.enums

enum class Settings(
    val path: String, // Cannot use getDescription because of JetBrains already using it somewhere
    val description: String,
    val visible: Boolean,
    private val type: String
) {
    DEBUG(
        "system.debug",
        "Use debug mode",
        true,
        "BOOL"
    ),
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
    );

    fun getType(): String {
        return "[$type]"
    }

    override fun toString(): String {
        return path
    }
}
