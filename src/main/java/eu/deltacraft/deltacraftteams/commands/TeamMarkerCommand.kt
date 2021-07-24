package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.managers.cache.TeamOwnerManager
import eu.deltacraft.deltacraftteams.types.IsTeamOwnerResponse
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class TeamMarkerCommand(
    private val plugin: Plugin,
    private val clientManager: ClientManager,
    private val teamOwnerManager: TeamOwnerManager,
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this commands")
            return false
        }

        val p: Player = sender

        if (!p.hasPermission(Permissions.TEAMMARKER)) {
            p.sendMessage(TextHelper.insufficientPermissions(Permissions.TEAMMARKER))
            return true
        }
        if (args.isEmpty() || args[0].isEmpty()) {
            p.sendMessage(ChatColor.GREEN.toString() + "Use " + ChatColor.YELLOW + "/teammarker ? " + ChatColor.GREEN + "for help")
            return true
        }

        val cmd = args[0].trim()

        if (cmd.equals("?", true) || cmd.equals("help", true)) {
            this.sendHelp(p)
            return true
        }

        if (args.size < 2) {
            p.sendMessage(ChatColor.YELLOW.toString() + "You must pass some arguments")
            return true
        }

        val arg = args.drop(1).joinToString(" ")

        if (cmd.equals("create", true) || cmd.equals("set", true)) {
            this.setMarker(p, arg)
            return true
        }

        if (cmd.equals("delete", true) || cmd.equals("remove", true)) {
            this.deleteMarker(p, arg)
            return true
        }

        return true
    }

    private fun setMarker(p: Player, name: String) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {
                setMarkerAsync(p, name)
            }
        })
        p.sendMessage(TextHelper.infoText("Checking if you are a owner..."))
    }

    private fun deleteMarker(p: Player, name: String) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {
                deleteMarkerAsync(p, name)
            }
        })
        p.sendMessage(TextHelper.infoText("Checking if you are a owner..."))
    }

    private suspend fun deleteMarkerAsync(p: Player, name: String) {
        val isOwner = checkIfIsOwnerAsync(p.uniqueId)
        if (!isOwner) {
            p.sendMessage(TextHelper.attentionText("You are not a team owner", NamedTextColor.RED))
            return
        }

    }

    private suspend fun setMarkerAsync(p: Player, name: String) {
        val isOwner = checkIfIsOwnerAsync(p.uniqueId)
        if (!isOwner) {
            p.sendMessage(TextHelper.attentionText("You are not a team owner", NamedTextColor.RED))
            return
        }

    }

    private suspend fun checkIfIsOwnerAsync(uuid: UUID): Boolean {
        val cache = teamOwnerManager[uuid]
        if (cache != null) {
            return cache.isOwner
        }
        val client = clientManager.getClient()

        val httpRes = client.get<HttpResponse>(path = "api/plugin/is-team-owner") {
            parameter("uuid", uuid.toString())
        }

        client.close()

        val status = httpRes.status

        if (status != HttpStatusCode.OK) {
            return false
        }

        val res = httpRes.receive<IsTeamOwnerResponse>()

        return teamOwnerManager.set(uuid, res.content).isOwner
    }


    private fun sendHelp(p: Player) {
        val text = Component.text("Team markers =========================")
            .append(Component.newline())
            .append(
                Component.text("/teammarker set <name>", NamedTextColor.YELLOW)
            )
            .append(
                Component.text(" Set team marker", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(
                Component.text("/teammarker remove <name>", NamedTextColor.YELLOW)
            )
            .append(
                Component.text(" Remove team marker", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(Component.text("===================================="))
        p.sendMessage(text)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): MutableList<String> {
        val list = mutableListOf<String>()

        if (!command.name.equals("teammarker", true)) {
            return list
        }

        if (!sender.hasPermission(Permissions.TEAMMARKER)) {
            return list
        }

        if (args.size < 2 || args[0].isEmpty()) {
            var typedIn = ""
            if (args.size == 1) {
                typedIn = args[0].lowercase()
            }
            val cmds = arrayOf("set", "remove")
            for (cmd in cmds) {
                if (cmd.startsWith(typedIn)) {
                    list.add(cmd)
                }
            }
            return list
        }

        return list
    }
}