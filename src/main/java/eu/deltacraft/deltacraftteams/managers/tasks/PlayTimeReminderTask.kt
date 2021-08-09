package eu.deltacraft.deltacraftteams.managers.tasks

import eu.deltacraft.deltacraftteams.managers.cache.JoinTimeCache
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.getInt
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PlayTimeReminderTask(private val plugin: JavaPlugin, private val joinTimeCache: JoinTimeCache) : Runnable {

    companion object {
        fun scheduleTask(plugin: JavaPlugin, joinTimeCache: JoinTimeCache) {
            Bukkit.getScheduler().runTaskTimer(
                plugin,
                PlayTimeReminderTask(plugin, joinTimeCache),
                0L, Constants.PLAY_TIME_CHECK * Constants.HOUR_TO_TICK
            )
        }
    }

    override fun run() {
        val minHours = plugin.config.getInt(Settings.PLAY_TIME_MIN)

        for ((uid, time) in joinTimeCache) {
            val player = plugin.server.getPlayer(uid) ?: continue
            if (!player.isOnline) continue

            // Player is online
            val currentTime = System.currentTimeMillis()
            if ((currentTime - time) <= minHours * Constants.HOUR_TO_MS) {
                continue
            }

            player.sendMessage(TextHelper.attentionText("Hraješ již více jak ${minHours}h, co si dát pauzu?"))

        }
    }


}