package eu.deltacraft.deltacraftteams

import eu.deltacraft.deltacraftteams.commands.MainCommand
import eu.deltacraft.deltacraftteams.commands.PvpZoneCommand
import eu.deltacraft.deltacraftteams.commands.home.DelHomeCommand
import eu.deltacraft.deltacraftteams.commands.home.HomeCommand
import eu.deltacraft.deltacraftteams.commands.home.SetHomeCommand
import eu.deltacraft.deltacraftteams.listeners.PlayerBlockListener
import eu.deltacraft.deltacraftteams.listeners.PlayerDeathEventListener
import eu.deltacraft.deltacraftteams.listeners.PlayerJoinAttemptListener
import eu.deltacraft.deltacraftteams.listeners.PvpZoneKillListener
import eu.deltacraft.deltacraftteams.managers.*
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import org.bukkit.plugin.java.JavaPlugin

class DeltaCraftTeams : JavaPlugin() {

    private var isDebug = false

    private lateinit var manager: DeltaCraftTeamsManager
    private lateinit var sentryManager: SentryManager
    private lateinit var pvpZoneManager: PvpZoneManager
    private lateinit var clientManager: ClientManager
    private lateinit var homesManager: HomesManager

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

        sentryManager.tryInitSentry()

        // Commands
        this.loadCommands()

        // Listeners
        this.loadListeners()

        val logger = server.consoleSender

        logger.sendMessage("DeltaCraft Teams ready!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        val logger = server.consoleSender
        logger.sendMessage("DeltaCraft Teams shutdown complete!")
    }

    private fun loadManagers() {
        manager = DeltaCraftTeamsManager(this)
        sentryManager = SentryManager(this)
        clientManager = ClientManager(this)

        pvpZoneManager = PvpZoneManager(this, manager.pvpZoneCacheManager)
        homesManager = HomesManager(this)
    }

    private fun loadCommands() {
        val mainCmd = this.getCommand("deltacraftteams")
        if (mainCmd != null) {
            mainCmd.aliases = listOf("delta", "deltacraft")
            mainCmd.setExecutor(MainCommand(this))
            debugMsg("Main command loaded")
        }
        val pvpZoneCmd = this.getCommand("pvp")
        if (pvpZoneCmd != null) {
            pvpZoneCmd.aliases = listOf("pvpzone", "pvpzones")
            pvpZoneCmd.setExecutor(PvpZoneCommand(this, pvpZoneManager))
            debugMsg("PvpZone command loaded")
        }
        loadHomeCommands()
    }

    private fun loadHomeCommands() {
        getCommand("sethome")!!.setExecutor(SetHomeCommand(homesManager))
        debugMsg("SetHome command loaded")
        getCommand("home")!!.setExecutor(HomeCommand(homesManager))
        debugMsg("Home command loaded")
        getCommand("delhome")!!.setExecutor(DelHomeCommand(homesManager))
        debugMsg("DelHome command loaded")
    }

    private fun loadListeners() {
        val pluginManager = this.server.pluginManager

        pluginManager.registerEvents(PlayerJoinAttemptListener(this, clientManager), this)
        this.debugMsg("PlayerJoinAttemptListener loaded")

        pluginManager.registerEvents(PlayerBlockListener(this), this)
        this.debugMsg("PlayerBlockListener loaded")

        pluginManager.registerEvents(PlayerDeathEventListener(manager), this)
        this.debugMsg("PlayerDeathEventListener loaded")

        pluginManager.registerEvents(PvpZoneKillListener(), this)
        this.debugMsg("PvpZoneKillListener loaded")
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

    fun debugMsg(message: String) {
        if (isDebug) {
            logger.info("[Debug]: $message")
        }
    }
}