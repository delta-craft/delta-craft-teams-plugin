package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.managers.cache.LoginCacheManager
import eu.deltacraft.deltacraftteams.types.NewLoginData
import eu.deltacraft.deltacraftteams.types.SessionResponse
import eu.deltacraft.deltacraftteams.utils.enums.ValidateError
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
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

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerAttemptJoinAsync(playerJoinEvent: AsyncPlayerPreLoginEvent) {
        runBlocking {
            val ip = playerJoinEvent.address.hostAddress
            val uuid = playerJoinEvent.uniqueId

            loginCacheManager.logoutPlayer(uuid)


            val client = clientManager.getClient()


            val httpRes1 =
                client.get<HttpResponse>("https://portal.deltacraft.eu/api/plugin/validate-session") {
                    parameter("ip", ip)
                    parameter("uuid", uuid.toString())
                }

            val status1 = httpRes1.status

            if (status1 != HttpStatusCode.OK && status1 != HttpStatusCode.BadRequest) {
                return@runBlocking
            }

            val sessionResponse = httpRes1.receive<SessionResponse>()

            if (sessionResponse.content) {
                loginCacheManager.loginPlayer(uuid)
                client.close()
                return@runBlocking;
            }

            // Request login attempt
            val httpRes2 =
                client.post<HttpResponse>("https://portal.deltacraft.eu/api/plugin/login") {
                    body = NewLoginData(uuid.toString(), ip)
                    contentType(ContentType.Application.Json)
                }

            val status2 = httpRes2.status

            if (status2 != HttpStatusCode.OK && status2 != HttpStatusCode.BadRequest) {
                return@runBlocking
            }

            val newLoginResponse = httpRes2.receive<SessionResponse>()

            client.close()

            if (!newLoginResponse.content) {
                loginCacheManager.loginPlayer(uuid)
                plugin.logger.info("Player ${playerJoinEvent.name} login request gone wrong")
                playerJoinEvent.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Component.text("Nastala chyba :("))
                return@runBlocking;
            }

            plugin.logger.info("Player ${playerJoinEvent.name} tried to join, but does not have an active session")

            playerJoinEvent.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                Component.text("Visit ")
                    .append(Component.text("DeltaCraft Portal ", NamedTextColor.DARK_AQUA))
                    .append(Component.text("and confirm your login."))
                    .append(Component.newline())
                    .append(Component.newline())
                    .append(
                        Component.text("https://portal.deltacraft.eu/login", NamedTextColor.DARK_AQUA).clickEvent(
                            ClickEvent.openUrl("https://portal.deltacraft.eu/login")
                        )
                    )
            )
            return@runBlocking;
        }
    }

    @EventHandler
    fun onPlayerQuit(playerQuitEvent: PlayerQuitEvent) {
        runBlocking {
            val uuid = playerQuitEvent.player.uniqueId
            val ip = playerQuitEvent.player.address.hostString

            if (!loginCacheManager.isLoggedIn(uuid)) {
                return@runBlocking
            }

            val client = clientManager.getClient()



        }
    }
}