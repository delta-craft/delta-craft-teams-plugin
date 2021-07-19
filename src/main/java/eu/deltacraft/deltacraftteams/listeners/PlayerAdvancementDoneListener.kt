package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.PointTag
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import java.util.*

class PlayerAdvancementDoneListener(
    private val plugin: DeltaCraftTeams,
    private val pointsQueue: PointsQueue
) : Listener {
    private val logger = plugin.logger

    @EventHandler
    fun onPlayerDoneAdvancement(playerAdvancementDoneEvent: PlayerAdvancementDoneEvent) {
        if(playerAdvancementDoneEvent.advancement.key.key.contains("recipes"))
            return

        val pointTags = mutableListOf(PointTag("AdvancementKey",playerAdvancementDoneEvent.advancement.key.value()))
        pointsQueue.registerPoint(
            Point(
                20,
                playerAdvancementDoneEvent.player.uniqueId,
                PointType.Journey,
                "Odemkl advancement: ${playerAdvancementDoneEvent.advancement.key}",
                tags = pointTags
            )
        )
    }
}