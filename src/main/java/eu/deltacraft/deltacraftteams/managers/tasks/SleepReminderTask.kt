package eu.deltacraft.deltacraftteams.managers.tasks

import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.utils.TextHelper
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class SleepReminderTask(private val plugin: JavaPlugin) : Runnable {

    companion object {
        fun scheduleTask(plugin: JavaPlugin) {
            Bukkit.getScheduler().runTaskTimer(
                plugin,
                SleepReminderTask(plugin),
                0L, Constants.SLEEP_TIME_CHECK * Constants.HOUR_TO_TICKS
            )
        }
    }

    override fun run() {

        val serverCalendar = Calendar.getInstance()

        val czCalendar = GregorianCalendar(TimeZone.getTimeZone("Europe/Prague"))
        czCalendar.timeInMillis = serverCalendar.timeInMillis

        val hour = czCalendar.get(Calendar.HOUR)

        if (hour >= Constants.SLEEP_MIN && hour <= Constants.SLEEP_MAX) {
            plugin.server.broadcast(TextHelper.attentionText("It's ${hour}AM, maybe you should sleep"))
        }

    }
}