package eu.deltacraft.deltacraftteams.types

data class TeamOwnerCache(val isOwner: Boolean, val cachedTime: Long = System.currentTimeMillis()) {

    companion object {
        private const val CACHE_TIME = 1 * 60 * 60 * 1000// 1h * 60min * 60s * 1000ms
    }

    val isExpired: Boolean
        get() = cachedTime + CACHE_TIME < System.currentTimeMillis()

}