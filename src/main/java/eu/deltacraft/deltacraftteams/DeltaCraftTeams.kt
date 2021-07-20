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
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin

class DeltaCraftTeams : JavaPlugin() {

    private lateinit var manager: DeltaCraftTeamsManager
    private lateinit var pvpZoneManager: PvpZoneManager
    private lateinit var clientManager: ClientManager
    private lateinit var homesManager: HomesManager
    private lateinit var pointsQueue: PointsQueue

    override fun onEnable() {
        // Plugin startup logic

        // Config
        this.loadConfig()

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
            infoMsg("Main command loaded")
        }
        val pvpZoneCmd = this.getCommand("pvp")
        if (pvpZoneCmd != null) {
            pvpZoneCmd.aliases = listOf("pvpzone", "pvpzones")
            pvpZoneCmd.setExecutor(PvpZoneCommand(this, pvpZoneManager))
            infoMsg("PvpZone command loaded")
        }

        val donationCmd = this.getCommand("donate")
        if (donationCmd != null) {
            donationCmd.aliases = listOf("donation")
            donationCmd.setExecutor(DonationCommand())
            infoMsg("Donation command loaded")
        }

        val pingCommand = this.getCommand("ping")
        if (pingCommand != null) {
            pingCommand.setExecutor(PingCommand())
            infoMsg("Ping command loaded")
        }


        loadHomeCommands()
    }

    private fun loadHomeCommands() {
        getCommand("sethome")!!.setExecutor(SetHomeCommand(homesManager))
        infoMsg("SetHome command loaded")
        getCommand("home")!!.setExecutor(HomeCommand(this, homesManager))
        infoMsg("Home command loaded")
        getCommand("delhome")!!.setExecutor(DelHomeCommand(homesManager))
        infoMsg("DelHome command loaded")
    }

    private fun loadListeners() {
        val pluginManager = this.server.pluginManager

        pluginManager.registerEvents(PlayerJoinAttemptListener(this, clientManager), this)
        this.infoMsg("PlayerJoinAttemptListener loaded")

        pluginManager.registerEvents(PlayerAdvancementDoneListener(this, pointsQueue), this)
        this.infoMsg("PlayerAdvancementDoneListener loaded")

        pluginManager.registerEvents(PlayerBlockListener(this), this)
        this.infoMsg("PlayerBlockListener loaded")

        pluginManager.registerEvents(PlayerChatListener(this), this)
        this.infoMsg("PlayerChatListener loaded")

        pluginManager.registerEvents(PlayerDeathEventListener(manager), this)
        this.infoMsg("PlayerDeathEventListener loaded")

        pluginManager.registerEvents(PvpZoneKillListener(), this)
        this.infoMsg("PvpZoneKillListener loaded")

        pluginManager.registerEvents(
            LoginListener(
                this,
                clientManager,
                manager.loginCacheManager,
                manager.teamCacheManager
            ), this
        )
        this.infoMsg("LoginListener loaded")

        pluginManager.registerEvents(PlayerHomeMoveListener(homesManager.homesCache), this)
        this.infoMsg("PlayerHomeMoveListener loaded")

        pluginManager.registerEvents(PortalListener(this), this)
        this.infoMsg("PortalListener loaded")

        pluginManager.registerEvents(ChatListener(this, clientManager), this)
        this.infoMsg("ChatListener loaded")
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

    private fun infoMsg(message: String) {
        logger.info("[DELTACRAFT]: $message")
    }
}