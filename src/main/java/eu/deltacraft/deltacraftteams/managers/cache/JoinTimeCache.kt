package eu.deltacraft.deltacraftteams.managers.cache

import eu.deltacraft.deltacraftteams.managers.templates.CacheManager
import java.util.*

class JoinTimeCache : CacheManager<UUID, Long>() {

    fun playerJoined(uid: UUID) {
        this[uid] = System.currentTimeMillis()
    }

    fun playerDisconnected(uid: UUID) {
        this.remove(uid)
    }

}