package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.managers.DeltaCraftTeamsManager
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneCacheManager
import eu.deltacraft.deltacraftteams.managers.cache.PvpZoneEnterCache
import eu.deltacraft.deltacraftteams.utils.TextHelper
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class PvpZoneEnterLeaveListener(
    private val zonesManager: PvpZoneCacheManager,
    private val zoneEnter: PvpZoneEnterCache,
) : Listener {
    constructor(manager: DeltaCraftTeamsManager) : this(manager.pvpZoneCacheManager, manager.zoneEnterCache)

    @EventHandler(ignoreCancelled = true)
    fun onMove(event: PlayerMoveEvent) {
        if (!event.hasChangedBlock()) {
            return
        }
        val player = event.player
        val uid = player.uniqueId

        val currentZone = zonesManager[player.location]

        val lastZoneName = zoneEnter[uid]

        if (lastZoneName == null) {
            if (currentZone == null) {
                // Last & Zone is null → Ignore
                return
            }
            // Last is null but zone is not null
            zoneEnter[uid] = currentZone
            player.sendActionBar(TextHelper.attentionText("Entering PVP zone '${currentZone.name}'"))
            return
        }

        // Last is not null
        if (currentZone == null) {
            zoneEnter.remove(uid)
            player.sendActionBar(TextHelper.attentionText("Leaving PVP zone"))
            return
        }

        // Last & Zone is not null

        if (lastZoneName != currentZone.name) {
            // Entered different zone
            zoneEnter.remove(uid)
            return
        }
    }

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        zoneEnter.remove(event.player)
    }
}