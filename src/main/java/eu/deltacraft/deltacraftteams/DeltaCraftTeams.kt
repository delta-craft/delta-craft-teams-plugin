package eu.deltacraft.deltacraftteams

import eu.deltacraft.deltacraftteams.commands.*
import eu.deltacraft.deltacraftteams.commands.home.DelHomeCommand
import eu.deltacraft.deltacraftteams.commands.home.HomeCommand
import eu.deltacraft.deltacraftteams.commands.home.SetHomeCommand
import eu.deltacraft.deltacraftteams.listeners.*
import eu.deltacraft.deltacraftteams.managers.*
import eu.deltacraft.deltacraftteams.types.TeamMarker
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin

class DeltaCraftTeams : JavaPlugin() {

    private lateinit var manager: DeltaCraftTeamsManager
    private lateinit var pvpZoneManager: PvpZoneManager
    private lateinit var clientManager: ClientManager
    private lateinit var homesManager: HomesManager
    private lateinit var pointsQueue: PointsQueue
    private lateinit var teamMarkerManager: TeamMarkerManager

    override fun onEnable() {
        // Plugin startup logic

        // Config serialization classes
        this.registerConfigClasses()

        // Config
        this.loadConfig()

        // Managers
        this.loadManagers()

        // Commands
        this.loadCommands()

        // Listeners
        this.loadListeners()

        pointsQueue.startTimer()

        logger.info("Loaded!")
    }

    override fun onDisable() {
        // Plugin shutdown logic

        // Invalidate sessions of all players on plugin shutdown
        logoutAll()

        sendAllPoints()

        logger.info("Disabled!")
    }

    private fun registerConfigClasses() {
        ConfigurationSerialization.registerClass(TeamMarker::class.java, "TeamMarker")
    }

    private fun loadManagers() {
        manager = DeltaCraftTeamsManager(this)
        clientManager = ClientManager(this)

        pvpZoneManager = PvpZoneManager(this, manager.pvpZoneCacheManager)
        homesManager = HomesManager(this)
        pointsQueue = PointsQueue(this, clientManager)
        teamMarkerManager = TeamMarkerManager(this, clientManager, manager)
    }

    private fun loadCommands() {
        val mainCmd = this.getCommand("deltacraftteams")
        if (mainCmd != null) {
            mainCmd.aliases = listOf("delta", "deltacraft")
            mainCmd.setExecutor(MainCommand(this, pointsQueue))
            logger.info("Main command loaded")
        }
        val pvpZoneCmd = this.getCommand("pvp")
        if (pvpZoneCmd != null) {
            pvpZoneCmd.aliases = listOf("pvpzone", "pvpzones")
            pvpZoneCmd.setExecutor(PvpZoneCommand(this, pvpZoneManager))
            logger.info("PvpZone command loaded")
        }

        val donationCmd = this.getCommand("donate")
        if (donationCmd != null) {
            donationCmd.aliases = listOf("donation")
            donationCmd.setExecutor(DonationCommand())
            logger.info("Donation command loaded")
        }

        val pingCommand = this.getCommand("ping")
        if (pingCommand != null) {
            pingCommand.setExecutor(PingCommand())
            logger.info("Ping command loaded")
        }

        val linksCommand = this.getCommand("links")
        if (linksCommand != null) {
            linksCommand.aliases = listOf("link")
            linksCommand.setExecutor(LinksCommand())
            logger.info("Links command loaded")
        }

        val teamMarkerCommand = this.getCommand("teammarker")
        if (teamMarkerCommand != null) {
            teamMarkerCommand.setExecutor(TeamMarkerCommand(teamMarkerManager))
            logger.info("Team marker command loaded")
        }


        loadHomeCommands()
    }

    private fun loadHomeCommands() {
        getCommand("sethome")!!.setExecutor(SetHomeCommand(homesManager))
        logger.info("SetHome command loaded")
        getCommand("home")!!.setExecutor(HomeCommand(this, homesManager, manager.pvpZoneCacheManager))
        logger.info("Home command loaded")
        getCommand("delhome")!!.setExecutor(DelHomeCommand(homesManager))
        logger.info("DelHome command loaded")
    }

    private fun loadListeners() {
        val pluginManager = this.server.pluginManager

        pluginManager.registerEvents(PlayerJoinAttemptListener(this, clientManager), this)
        logger.info("PlayerJoinAttemptListener loaded")

        pluginManager.registerEvents(PlayerAdvancementDoneListener(this, pointsQueue), this)
        logger.info("PlayerAdvancementDoneListener loaded")

        pluginManager.registerEvents(PlayerBlockListener(pointsQueue), this)
        logger.info("PlayerBlockListener loaded")

        pluginManager.registerEvents(PlayerChatListener(this), this)
        logger.info("PlayerChatListener loaded")

        pluginManager.registerEvents(PlayerDeathEventListener(manager), this)
        logger.info("PlayerDeathEventListener loaded")

        pluginManager.registerEvents(PvpZoneKillListener(pointsQueue), this)
        logger.info("PvpZoneKillListener loaded")

        pluginManager.registerEvents(LoginListener(this, clientManager, manager.loginCacheManager), this)
        logger.info("LoginListener loaded")

        pluginManager.registerEvents(PlayerHomeMoveListener(homesManager.homesCache), this)
        logger.info("PlayerHomeMoveListener loaded")

        pluginManager.registerEvents(PortalListener(this), this)
        logger.info("PortalListener loaded")

        pluginManager.registerEvents(ChatListener(this, clientManager), this)
        logger.info("ChatListener loaded")

        pluginManager.registerEvents(PvpZoneEnterLeaveListener(manager), this)
        logger.info("PvpZoneEnterLeaveListener loaded")

        pluginManager.registerEvents(PlayerSuccessJoinListener(manager.teamCacheManager), this)
        logger.info("PlayerSuccessJoinListener loaded")

        pluginManager.registerEvents(MobDamageListener(manager.mobDamageCache, pointsQueue), this)
        logger.info("MobDamageListener loaded")

        pluginManager.registerEvents(ShulkerKillListener(), this)
        logger.info("ShulkerKillListener loaded")

        pluginManager.registerEvents(CraftItemListener(pointsQueue), this)
        logger.info("CraftItemListener loaded")

        pluginManager.registerEvents(AnvilRenameListener(this, clientManager), this)
        logger.info("AnvilListener loaded")

        pluginManager.registerEvents(SmithItemListener(pointsQueue), this)
        logger.info("SmithItemListener loaded")
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
            pointsQueue.trySendAllPointsAsync()
        }
    }

    private fun logoutAll() {
        val client = clientManager.getClient()

        runBlocking {
            val res = client.post<HttpResponse>(path = "login/logout")

            val status = res.status

            if (status != HttpStatusCode.OK) {
                logger.warning("Logout-All request returned HTTP ${status.value}")
            }
        }

        client.close()
    }

}