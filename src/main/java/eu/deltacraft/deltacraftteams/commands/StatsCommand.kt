package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager
import eu.deltacraft.deltacraftteams.managers.chest.StatsGuiIntegration
import eu.deltacraft.deltacraftteams.types.StatsContent
import eu.deltacraft.deltacraftteams.types.responses.StatsResponse
import eu.deltacraft.deltacraftteams.utils.TextHelper
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class StatsCommand(
    private val plugin: JavaPlugin,
    private val clientManager: ClientManager,
    private val teamManager: TeamCacheManager,
) : CommandExecutor, TabCompleter {
    private val integration = StatsGuiIntegration(plugin)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this commands")
            return false
        }
        val p: Player = sender
        if (args.isEmpty() || args[0].isEmpty()) {
            return prepareStatsLoad(p)
        }
        val playerName = args[0].trim()

        return prepareStatsLoad(p, playerName)
    }

    private fun prepareStatsLoad(p: Player): Boolean {
        return prepareStatsLoad(p, p.name)
    }

    private fun prepareStatsLoad(p: Player, playerName: String): Boolean {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {
                val stats = getStatsAsync(playerName)
                openStats(p, stats)
            }
        })
        return true
    }

    private fun openStats(p: Player, stats: StatsResponse?) {
        if (stats == null || !stats.content.success || stats.content.stats == null) {
            p.sendMessage(TextHelper.attentionText("Error loading stats."))
            plugin.logger.warning("Error loading stats. Error: ${stats?.error}. Message: ${stats?.message}")
            return
        }
        openStatsInternal(p, stats.content)
    }

    private fun openStatsInternal(p: Player, content: StatsContent) {
        val stats = content.stats ?: return
        val playerName = content.player

        val pUuid = Bukkit.getOfflinePlayerIfCached(playerName)?.uniqueId

        val color = if (pUuid == null) NamedTextColor.GRAY else (teamManager[pUuid]?.majorTeamEnum?.color
            ?: NamedTextColor.GRAY)

        integration.showGui(p, playerName, color, stats)
    }

    private suspend fun getStatsAsync(playerName: String): StatsResponse? {
        val client = clientManager.getClient()

        val httpRes = client.get<HttpResponse>(path = "stats") {
            parameter("name", playerName)
        }

        client.close()

        val status = httpRes.status

        if (status != HttpStatusCode.OK && status != HttpStatusCode.BadRequest) {
            return null
        }
        return httpRes.receive<StatsResponse>()
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): MutableList<String> {
        return Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
    }
}