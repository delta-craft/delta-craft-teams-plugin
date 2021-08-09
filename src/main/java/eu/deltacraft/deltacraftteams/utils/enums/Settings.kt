package eu.deltacraft.deltacraftteams.utils.enums

enum class Settings(
    val path: String, // Cannot use getDescription because of JetBrains already using it somewhere
) {
    APIKEY("system.apikey"),
    PAYLOADSIZE("system.payloadSize"),
    HOMEDELAY("settings.home.delay"),
    MAXTEAMMARKERS("settings.teammarkers.max"),
    SLEEP_MIN("settings.sleep.min"),
    SLEEP_MAX("settings.sleep.max"),
    PLAY_TIME_MIN("settings.playTime.min");

    override fun toString(): String {
        return path
    }
}
