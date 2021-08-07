package eu.deltacraft.deltacraftteams.types

import java.util.UUID


class KeyHelper(id: String, prefix: String = "players") {
    constructor(id: UUID, prefix: String = "players") : this(id.toString(), prefix)

    operator fun get(vararg subkeys: String): String {
        return "$key.${subkeys.joinToString(".")}"
    }

    val key = "$prefix.$id"

}