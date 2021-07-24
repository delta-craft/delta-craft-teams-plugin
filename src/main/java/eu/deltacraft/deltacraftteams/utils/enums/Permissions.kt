package eu.deltacraft.deltacraftteams.utils.enums

enum class Permissions(var path: String) {
    USEMAIN("main"),
    PVPMANAGE("pvp"),
    TEAMMARKER("teammarker"),
    HOME("home");


    override fun toString(): String {
        return path
    }

    init {
        path = "delta.$path"
    }
}