package eu.deltacraft.deltacraftteams

import eu.deltacraft.deltacraftteams.listeners.PlayerBlockListener
import org.bukkit.plugin.java.JavaPlugin

class DeltaCraftTeams : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        this.loadListeners()

        val logger = server.consoleSender

        logger.sendMessage("DeltaCraft Teams ready!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        val logger = server.consoleSender
        logger.sendMessage("DeltaCraft Teams shutdown complete!")
    }

    private fun loadListeners() {
        val pluginManager = this.server.pluginManager

        pluginManager.registerEvents(PlayerBlockListener(this), this)
    }
}