package eu.deltacraft.deltacraftteams

import eu.deltacraft.deltacraftteams.commands.MainCommand
import eu.deltacraft.deltacraftteams.interfaces.IDbConnector
import eu.deltacraft.deltacraftteams.listeners.PlayerBlockListener
import eu.deltacraft.deltacraftteams.listeners.PlayerJoinListener
import eu.deltacraft.deltacraftteams.managers.DeltaCraftTeamsManager
import eu.deltacraft.deltacraftteams.types.DbConnector
import eu.deltacraft.deltacraftteams.types.getString
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.sentry.Sentry
import io.sentry.SentryLevel
import org.bukkit.plugin.java.JavaPlugin

class DeltaCraftTeams : JavaPlugin() {

    private var isDebug = false

    private lateinit var dbConnector: IDbConnector

    lateinit var manager: DeltaCraftTeamsManager
        private set


    override fun onEnable() {
        // Plugin startup logic

        // Config
        this.loadConfig()
        isDebug = config.getBoolean(Settings.DEBUG.path)
        if (isDebug) {
            this.debugMsg("Debugging enabled")
        }

        this.tryInitSentry()

        dbConnector = DbConnector(config)

        // Managers
        this.loadManagers()

        // Commands
        this.loadCommands()

        // Listeners
        this.loadListeners()

        val logger = server.consoleSender

        val res = DbConn(this, dbConnector).getUsers()

        this.debugMsg(res)

        logger.sendMessage("DeltaCraft Teams ready!")
    }

    private fun tryInitSentry() {
        val dsn = config.getString(Settings.SENTRY)
        if (dsn.isNullOrEmpty()) {
            return
        }

        try {
            Sentry.init { x ->
                run {
                    x.dsn = dsn
                    x.tracesSampleRate = 1.0
                    x.setDebug(isDebug)
                }
            }
            debugMsg("Sentry enabled")
        } catch (e: Exception) {
        }
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
        pluginManager.registerEvents(PlayerJoinListener(this, dbConnector), this)
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