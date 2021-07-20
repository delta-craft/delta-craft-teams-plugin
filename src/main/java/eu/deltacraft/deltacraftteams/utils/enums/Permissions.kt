package eu.deltacraft.deltacraftteams.utils.enums

enum class Permissions(var path: String) {
    USEMAIN("main"),
    PVPCREATE("pvp.create"),
    PVPREMOVE("pvp.remove"),

    HOME("home");

    override fun toString(): String {
        return path
    }

    init {
        path = "deltateams.$path"
    }
}