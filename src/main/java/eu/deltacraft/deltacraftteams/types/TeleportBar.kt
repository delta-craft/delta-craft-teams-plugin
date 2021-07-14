package eu.deltacraft.deltacraftteams.types

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TeleportBar(private val plugin: JavaPlugin) {

    private val barText = Component.text("Teleporting", NamedTextColor.AQUA)

    private val bossBar: BossBar = BossBar.bossBar(barText, 1F, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)

    private var audience: Audience? = null

    private var barTaskId: Int = -1
    var mainTaskId: Int = -1


    fun showBar(player: Player, length: Int) {
        audience = player
        audience!!.showBossBar(bossBar)

        val time = 1.0 / (length * 20)

        barTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if (bossBar.progress() - time <= 0.0) {
                Bukkit.getScheduler().cancelTask(barTaskId)
            } else {
                bossBar.progress((bossBar.progress() - time).toFloat())
            }
        }, 0, 0)

    }

    fun hideBar() {
        audience?.hideBossBar(bossBar)
        val scheduler = Bukkit.getScheduler()
        if (barTaskId > 1) {
            scheduler.cancelTask(barTaskId)
        }
        if (mainTaskId > 1) {
            scheduler.cancelTask(mainTaskId)
        }
    }

}