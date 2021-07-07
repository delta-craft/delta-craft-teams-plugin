package eu.deltacraft.deltacraftteams.utils.enums

enum class Permissions(var path: String) {
    SHOWVERSION("main.version"),
    CONFIGSHOW("main.show"),
    CONFIGRELOAD("main.reload"),
    CONFIGCHANGE("main.change");

    override fun toString(): String {
        return path
    }

    init {
        path = "deltateams.$path";
    }
}