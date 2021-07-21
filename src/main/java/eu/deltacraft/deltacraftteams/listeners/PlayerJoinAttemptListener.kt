package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.types.disallow
import eu.deltacraft.deltacraftteams.types.responses.ConnectionResponse
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.ValidateError
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PlayerJoinAttemptListener(
    private val plugin: DeltaCraftTeams,
    private val clientManager: ClientManager
) : Listener {
    private val logger = plugin.logger

    @EventHandler
    fun onPlayerAttemptJoinAsync(playerJoinEvent: AsyncPlayerPreLoginEvent) {
        runBlocking {
            val client = clientManager.getClient()

            val httpRes =
                client.get<HttpResponse>(path = "api/plugin/validate") {
                    parameter("nick", playerJoinEvent.name)
                    parameter("uuid", playerJoinEvent.uniqueId)
                }

            client.close()

            val status = httpRes.status

            if (status != HttpStatusCode.OK && status != HttpStatusCode.BadRequest) {
                logger.warning("Validate request for player ${playerJoinEvent.name} returned HTTP ${status.value}")
                playerJoinEvent.disallow(status)
                return@runBlocking
            }

            val response = httpRes.receive<ConnectionResponse>()

            if (!response.content) {
                plugin.logger.warning("Player ${playerJoinEvent.name} tried to join, but error occurred: \"${response.error}\"")

                val message = when (response.getErrorEnum()) {
                    ValidateError.NotRegistered ->
                        Component.text("You have to be registered!")
                            .append(Component.newline())
                            .append(TextHelper.visitUrl("register", ""))
                    ValidateError.MissingConsent ->
                        Component.text("You have to accept our consent!")
                            .append(Component.newline())
                            .append(TextHelper.visitUrl("accept our consent", "consents"))
                    ValidateError.NotInTeam ->
                        Component.text("You have to join a team!")
                            .append(Component.newline())
                            .append(TextHelper.visitUrl("join a team", "teams"))
                    else -> {
                        Component.text("Server error :-(")
                    }
                }

                playerJoinEvent.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                    message
                )
            }
        }
    }


}