package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.managers.TeamMarkerManager
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class TeamMarkerCommand(
    private val teamMarkerManager: TeamMarkerManager,
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

        if (cmd.equals("list", true)) {
            this.listForTeam(p)
            return true
        }

        if (cmd.equals("list-all", true) && p.hasPermission(Permissions.TEAMMARKERADMIN)) {
            this.listAll(p)
            return true
        }

        if (args.size < 2) {
            p.sendMessage(ChatColor.YELLOW.toString() + "You must pass some arguments")
            return true
        }

        val id = args[1].trim()

        if (cmd.equals("delete", true) || cmd.equals("remove", true)) {
            teamMarkerManager.deleteMarker(p, id)
            return true
        }

        if (cmd.equals("create", true) || cmd.equals("set", true)) {
            val name = args.drop(1).joinToString(" ")
            teamMarkerManager.setMarker(p, name)
            return true
        }

        if (cmd.equals("description", true) || cmd.equals("desc", true)) {
            val description = args.drop(2).joinToString(" ")
            teamMarkerManager.setDescription(p, id, description)
            return true
        }

        return true
    }

    private fun listAll(p: Player) {
        val allMarkers = teamMarkerManager.getAllMarkers()

        TODO("Not yet implemented")
    }

    private fun listForTeam(p: Player) {
        val allMarkers = teamMarkerManager.getTeamMarkers(p)

        TODO("Not yet implemented")
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
            .append(
                Component.text("/teammarker list", NamedTextColor.YELLOW)
            )
            .append(
                Component.text(" Show your team markers", NamedTextColor.GREEN)
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
            val cmds = mutableListOf("set", "remove, list", "description")
            if (sender.hasPermission(Permissions.TEAMMARKERADMIN)) {
                cmds.add("list-all")
            }
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