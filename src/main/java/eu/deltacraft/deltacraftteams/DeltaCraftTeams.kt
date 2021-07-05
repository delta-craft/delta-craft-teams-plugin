package eu.deltacraft.deltacraftteams

import eu.deltacraft.deltacraftteams.commands.MainCommand
import eu.deltacraft.deltacraftteams.listeners.PlayerBlockListener
import eu.deltacraft.deltacraftteams.managers.DeltaCraftTeamsManager
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import org.bukkit.plugin.java.JavaPlugin

class DeltaCraftTeams : JavaPlugin() {

    private var isDebug = false
    private lateinit var manager: DeltaCraftTeamsManager


    override fun onEnable() {
        // Plugin startup logic

        // Config
        this.loadConfig()
        isDebug = config.getBoolean(Settings.DEBUG.path)
        if (isDebug) {
            this.debugMsg("Debugging enabled")
        }

        // Managers
        this.loadManagers()

        // Commands
        this.loadCommands()

        // Listeners
        this.loadListeners()

        val logger = server.consoleSender

        val res = DbConn(this).getUsers()

        this.debugMsg(res)

        logger.sendMessage("DeltaCraft Teams ready!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        val logger = server.consoleSender
        logger.sendMessage("DeltaCraft Teams shutdown complete!")
    }

    private fun loadManagers() {
        manager = DeltaCraftTeamsManager(this)
    }

    private fun loadCommands() {
        val mainCmd = this.getCommand("deltacraftteams")
        if (mainCmd != null) {
            mainCmd.aliases = listOf("delta", "deltacraft")
            mainCmd.setExecutor(MainCommand(this))
        }
    }

    private fun loadListeners() {
        val pluginManager = this.server.pluginManager

        pluginManager.registerEvents(PlayerBlockListener(this), this)
        this.debugMsg("PlayerBlockListener loaded")
    }

    private fun loadConfig() {
        saveDefaultConfig()
        config.options().copyDefaults(true)
        config.options().header(getHeader())
        saveConfig()
    }

    private fun getHeader(): String {
        val sep = System.getProperty("line.separator")
        return ("###################" + sep
                + "DeltaCraftTeams v." + description.version + sep
                + "###################")
    }

    fun setDebug(debug: Boolean): Boolean {
        isDebug = debug
        config[Settings.DEBUG.path] = isDebug
        saveConfig()
        return isDebug
    }

    private fun debugMsg(message: String) {
        if (isDebug) {
            logger.info("[Debug]: $message")
        }
    }
}