package eu.deltacraft.deltacraftteams

import eu.deltacraft.deltacraftteams.commands.DonationCommand
import eu.deltacraft.deltacraftteams.commands.MainCommand
import eu.deltacraft.deltacraftteams.commands.PingCommand
import eu.deltacraft.deltacraftteams.commands.PvpZoneCommand
import eu.deltacraft.deltacraftteams.commands.home.DelHomeCommand
import eu.deltacraft.deltacraftteams.commands.home.HomeCommand
import eu.deltacraft.deltacraftteams.commands.home.SetHomeCommand
import eu.deltacraft.deltacraftteams.listeners.*
import eu.deltacraft.deltacraftteams.managers.*
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

class DeltaCraftTeams : JavaPlugin() {

    private var isDebug = false

    private lateinit var manager: DeltaCraftTeamsManager
    private lateinit var pvpZoneManager: PvpZoneManager
    private lateinit var clientManager: ClientManager
    private lateinit var homesManager: HomesManager
    private lateinit var pointsQueue: PointsQueue

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

        pointsQueue.startTimer()

        val logger = server.consoleSender

        logger.sendMessage("DeltaCraft Teams ready!")
    }

    override fun onDisable() {
        // Plugin shutdown logic

        // Invalidate sessions of all players on plugin shutdown
        logoutAll()

        sendAllPoints()

        val logger = server.consoleSender
        logger.sendMessage("DeltaCraft Teams shutdown complete!")
    }

    private fun loadManagers() {
        manager = DeltaCraftTeamsManager(this)
        clientManager = ClientManager(this)

        pvpZoneManager = PvpZoneManager(this, manager.pvpZoneCacheManager)
        homesManager = HomesManager(this)
        pointsQueue = PointsQueue(this, clientManager)
    }

    private fun loadCommands() {
        val mainCmd = this.getCommand("deltacraftteams")
        if (mainCmd != null) {
            mainCmd.aliases = listOf("delta", "deltacraft")
            mainCmd.setExecutor(MainCommand(this, pointsQueue))
            debugMsg("Main command loaded")
        }
        val pvpZoneCmd = this.getCommand("pvp")
        if (pvpZoneCmd != null) {
            pvpZoneCmd.aliases = listOf("pvpzone", "pvpzones")
            pvpZoneCmd.setExecutor(PvpZoneCommand(this, pvpZoneManager))
            debugMsg("PvpZone command loaded")
        }

        val donationCmd = this.getCommand("donate")
        if (donationCmd != null) {
            donationCmd.aliases = listOf("donation")
            donationCmd.setExecutor(DonationCommand())
            debugMsg("Donation command loaded")
        }

        val pingCommand = this.getCommand("ping")
        if (pingCommand != null) {
            pingCommand.setExecutor(PingCommand())
            debugMsg("Ping command loaded")
        }


        loadHomeCommands()
    }

    private fun loadHomeCommands() {
        getCommand("sethome")!!.setExecutor(SetHomeCommand(homesManager))
        debugMsg("SetHome command loaded")
        getCommand("home")!!.setExecutor(HomeCommand(this, homesManager))
        debugMsg("Home command loaded")
        getCommand("delhome")!!.setExecutor(DelHomeCommand(homesManager))
        debugMsg("DelHome command loaded")
    }

    private fun loadListeners() {
        val pluginManager = this.server.pluginManager

        pluginManager.registerEvents(PlayerJoinAttemptListener(this, clientManager), this)
        this.debugMsg("PlayerJoinAttemptListener loaded")

        pluginManager.registerEvents(PlayerAdvancementDoneListener(this, pointsQueue), this)
        this.debugMsg("PlayerAdvancementDoneListener loaded")

        pluginManager.registerEvents(PlayerBlockListener(this), this)
        this.debugMsg("PlayerBlockListener loaded")

        pluginManager.registerEvents(PlayerChatListener(this), this)
        this.debugMsg("PlayerChatListener loaded")

        pluginManager.registerEvents(PlayerDeathEventListener(manager), this)
        this.debugMsg("PlayerDeathEventListener loaded")

        pluginManager.registerEvents(PvpZoneKillListener(), this)
        this.debugMsg("PvpZoneKillListener loaded")

        pluginManager.registerEvents(LoginListener(this, clientManager, manager.loginCacheManager), this)
        this.debugMsg("LoginListener loaded")

        pluginManager.registerEvents(PlayerHomeMoveListener(homesManager.homesCache), this)
        this.debugMsg("PlayerHomeMoveListener loaded")

        pluginManager.registerEvents(PortalListener(this), this)
        this.debugMsg("PortalListener loaded")

        pluginManager.registerEvents(ChatListener(this, clientManager), this)
        this.debugMsg("ChatListener loaded")
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

    private fun sendAllPoints() {

        runBlocking {
            pointsQueue.sendAllPointsAsync()
        }
    }

    private fun logoutAll() {
        val client = clientManager.getClient()

        runBlocking {
            val res = client.post<HttpResponse>(path = "api/plugin/logout-all")

            val status = res.status

            if (status != HttpStatusCode.OK) {
                logger.warning("Logout-All request returned HTTP ${status.value}")
            }
        }

        client.close()
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