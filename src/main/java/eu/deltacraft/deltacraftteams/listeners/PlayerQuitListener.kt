package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.managers.DeltaCraftTeamsManager
import eu.deltacraft.deltacraftteams.managers.cache.JoinTimeCache
import eu.deltacraft.deltacraftteams.managers.cache.LoginCacheManager
import eu.deltacraft.deltacraftteams.types.NewLoginData
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener(
    private val plugin: DeltaCraftTeams,
    private val clientManager: ClientManager,
    private val loginCacheManager: LoginCacheManager,
    private val joinTimeCache: JoinTimeCache,
) : Listener {
    constructor(plugin: DeltaCraftTeams, clientManager: ClientManager, manager: DeltaCraftTeamsManager) : this(plugin,
        clientManager,
        manager.loginCacheManager,
        manager.joinTimeCache)

    private val logger = plugin.logger

    @EventHandler
    fun onPlayerQuit(playerQuitEvent: PlayerQuitEvent) {
        val player = playerQuitEvent.player
        val uuid = player.uniqueId
        val ip = player.address.hostString

        joinTimeCache.playerJoined(uuid)

        if (!loginCacheManager.isLoggedIn(uuid)) {
            return
        }

        loginCacheManager.logoutPlayer(uuid)

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            val client = clientManager.getClient()

            runBlocking {

                val httpRes = client.post<HttpResponse>(path = "session/update") {
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