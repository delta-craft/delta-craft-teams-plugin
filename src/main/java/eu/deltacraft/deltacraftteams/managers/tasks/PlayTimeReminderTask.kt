package eu.deltacraft.deltacraftteams.managers.tasks

import eu.deltacraft.deltacraftteams.managers.cache.JoinTimeCache
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.utils.TextHelper
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PlayTimeReminderTask(private val plugin: JavaPlugin, private val joinTimeCache: JoinTimeCache) : Runnable {

    companion object {
        fun scheduleTask(plugin: JavaPlugin, joinTimeCache: JoinTimeCache) {
            Bukkit.getScheduler().runTaskTimer(
                plugin,
                PlayTimeReminderTask(plugin, joinTimeCache),
                0L, Constants.PLAY_TIME_CHECK * Constants.HOUR_TO_TICKS
            )
        }
    }

    override fun run() {
        for ((uid, time) in joinTimeCache) {
            val player = plugin.server.getPlayer(uid) ?: continue
            if (!player.isOnline) continue

            // Player is online
            val currentTime = System.currentTimeMillis()
            if ((currentTime - time) <= Constants.PLAY_TIME_REMINDER * 60 * 60 * 1000) {
                continue
            }

            player.sendMessage(TextHelper.attentionText("Hraješ již více jak ${Constants.PLAY_TIME_REMINDER}h, co si dát pauzu?"))

        }
    }


}