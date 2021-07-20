package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.types.CheckChatResult
import eu.deltacraft.deltacraftteams.types.PointsResult
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.PointsError
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener(private val plugin: DeltaCraftTeams, private val clientManager: ClientManager) : Listener {

    /**
     * Family Friendly Chat
     */

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {

        val p = event.player

        if (event.originalMessage() !is TextComponent)
            return

        val chat = event.originalMessage() as TextComponent

        val msg = chat.content()

        val client = clientManager.getClient()

        runBlocking {

            val httpRes = client.get<HttpResponse>(path = "api/plugin/check-chat") {
                parameter("message", msg)
                parameter("uuid", p.uniqueId.toString())
            }

            client.close()

            val status = httpRes.status

            if (status != HttpStatusCode.OK && status != HttpStatusCode.BadRequest) {
                return@runBlocking
            }

            val res = httpRes.receive<CheckChatResult>()

            if (res.content) return@runBlocking

            event.isCancelled = true

            plugin.logger.info("${p.name} se neumí chovat v chatu (${res.message})")

            p.sendMessage(
                TextHelper.infoText("Snažíme se udržovat family-friendly chat.")
                    .append(Component.newline())
                    .append(Component.text("Vyvaruj se následujícím slovům:"))
                    .append(Component.newline())
                    .append(Component.text("${res.message}", NamedTextColor.GRAY))
            )

        }
    }
}