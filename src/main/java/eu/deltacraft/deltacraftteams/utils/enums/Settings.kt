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
    SENTRY(
        "system.sentry",
        "Sentry DSN",
        false,
        "STRING"
    ),
    DATABASESERVER(
        "system.database.server",
        "Database server",
        false,
        "STRING"
    ),
    DATABASENAME(
        "system.database.name",
        "Database name",
        false,
        "STRING"
    ),
    DATABASELOGIN(
        "system.database.username",
        "Database server login name ",
        false,
        "STRING"
    ),
    DATABASEPASSWORD(
        "system.database.password",
        "Database server password",
        false,
        "STRING"
    );
    /*,
    SPECTATEMAXDISTANCE(
        "settings.spectate.maxdistance",
        "Maximum distance can player travel in spectator mode from starting point",
        false,
        "INT"
    );*/

    fun getType(): String {
        return "[$type]"
    }

    override fun toString(): String {
        return path
    }
}
