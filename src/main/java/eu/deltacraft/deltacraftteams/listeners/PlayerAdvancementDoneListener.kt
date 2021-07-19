package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.utils.enums.PointType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class PlayerAdvancementDoneListener(
    private val plugin: DeltaCraftTeams,
    private val pointsQueue: PointsQueue
) : Listener {
    private val logger = plugin.logger

    @EventHandler
    fun onPlayerDoneAdvancement(event: PlayerAdvancementDoneEvent) {
        if (event.advancement.key.key.contains("recipes"))
            return

        val player = event.player

        val point = Point(
            20,
            player.uniqueId,
            PointType.Journey,
            "Odemkl advancement: ${event.advancement.key}"
        )

        point.addTag("AdvancementKey", event.advancement.key.value())

        pointsQueue.registerPoint(point)
    }
}