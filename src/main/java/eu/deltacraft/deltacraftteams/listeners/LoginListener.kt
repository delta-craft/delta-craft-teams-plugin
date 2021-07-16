package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.managers.cache.LoginCacheManager
import eu.deltacraft.deltacraftteams.types.NewLoginData
import eu.deltacraft.deltacraftteams.types.SessionResponse
import eu.deltacraft.deltacraftteams.utils.TextHelper
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class LoginListener(
    private val plugin: DeltaCraftTeams,
    private val clientManager: ClientManager,
    private val loginCacheManager: LoginCacheManager
) : Listener {

    private val logger = plugin.logger

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerAttemptJoinAsync(playerJoinEvent: AsyncPlayerPreLoginEvent) {
        if (playerJoinEvent.loginResult != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return
        }

        val ip = playerJoinEvent.address.hostAddress
        val uuid = playerJoinEvent.uniqueId

        loginCacheManager.logoutPlayer(uuid)

        val client = clientManager.getClient()

        runBlocking {

            val validateResult = client.get<HttpResponse>(path = "api/plugin/validate-session") {
                parameter("ip", ip)
                parameter("uuid", uuid.toString())
            }

            val validateStatus = validateResult.status

            if (validateStatus != HttpStatusCode.OK && validateStatus != HttpStatusCode.BadRequest) {
                client.close()
                logger.warning("Validate session request for player ${playerJoinEvent.name} returned HTTP ${validateStatus.value}")
                return@runBlocking
            }

            val sessionResponse = validateResult.receive<SessionResponse>()

            if (sessionResponse.content) {
                loginCacheManager.loginPlayer(uuid)
                logger.info("Player ${playerJoinEvent.name} joined because of an active session")
                client.close()
                return@runBlocking
            }

            // Request login attempt
            val loginResult =
                client.post<HttpResponse>(path = "api/plugin/login") {
                    body = NewLoginData(uuid.toString(), ip)
                    contentType(ContentType.Application.Json)
                }

            client.close()

            val loginStatus = loginResult.status

            if (loginStatus != HttpStatusCode.OK && loginStatus != HttpStatusCode.BadRequest) {
                logger.warning("Player ${playerJoinEvent.name} login request returned HTTP ${loginStatus.value}")
                return@runBlocking
            }

            val newLoginResponse = loginResult.receive<SessionResponse>()

            if (!newLoginResponse.content) {
                logger.warning("Player ${playerJoinEvent.name} login request gone wrong")
                playerJoinEvent.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    Component.text("Nastala chyba :(")
                )
                return@runBlocking
            }

            logger.info("Player ${playerJoinEvent.name} tried to join, but does not have an active session")

            playerJoinEvent.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                TextHelper.visitUrl("confirm your login", "login")
            )
        }
    }

    @EventHandler
    fun onPlayerQuit(playerQuitEvent: PlayerQuitEvent) {
        val player = playerQuitEvent.player
        val uuid = player.uniqueId
        val ip = player.address.hostString

        if (!loginCacheManager.isLoggedIn(uuid)) {
            return
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val client = clientManager.getClient()

            runBlocking {

                val httpRes = client.post<HttpResponse>(path = "api/plugin/update-session") {
                    body = NewLoginData(uuid.toString(), ip)
                    contentType(ContentType.Application.Json)
                }

                val status = httpRes.status

                if (status != HttpStatusCode.OK) {
                    logger.warning("Update session for user ${player.name} returned HTTP ${status.value}")
                }

            }

            client.close()

            // Není potřeba logika, prostě se to pokusilo updatnout session :))
            // val newLoginResponse = httpRes.receive<SessionResponse>()
        })

    }
}