package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.managers.TeamMarkerManager
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
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

        return true
    }

    private fun listAll(p: Player) {
        val allMarkers = teamMarkerManager.getAllMarkers()

        var text = Component.empty()
            .append(
                TextHelper.infoText("All team markers:")
            )
            .append(TextHelper.getDivider())

        val query = allMarkers.groupBy { x -> x.teamId }
        for (group in query) {
            val markes = group.value

            text = text.append(
                Component.text("Markers for team with ID '${group.key}':")
            ).append(Component.newline())

            for (marker in markes) {
                text = text.append(marker.getInfo())
                if (marker.id != markes.last().id) {
                    text = text.append(Component.newline())
                }
            }
        }

        text = text.append(TextHelper.getDivider())

        p.sendMessage(text)
    }

    private fun listForTeam(p: Player) {
        val allMarkers = teamMarkerManager.getTeamMarkers(p)

        var text = Component.empty()
            .append(
                TextHelper.infoText("Team markers:")
            )
            .append(TextHelper.getDivider())

        for (marker in allMarkers) {
            text = text.append(marker.getInfo())
            if (marker.id != allMarkers.last().id) {
                text = text.append(Component.newline())
            }
        }

        text = text.append(TextHelper.getDivider())

        p.sendMessage(text)
    }

    private fun sendHelp(p: Player) {
        val text = Component.text("Team markers =========================")
            .append(Component.newline())
            .append(
                Component.text("/teammarker set <name>", NamedTextColor.YELLOW)
                    .clickEvent(
                        ClickEvent.suggestCommand("/teammarker set ")
                    )
            )
            .append(
                Component.text(" Set team marker", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(
                Component.text("/teammarker remove <id>", NamedTextColor.YELLOW)
                    .clickEvent(
                        ClickEvent.suggestCommand("/teammarker remove ")
                    )
            )
            .append(
                Component.text(" Remove team marker", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(
                Component.text("/teammarker list", NamedTextColor.YELLOW)
                    .clickEvent(
                        ClickEvent.suggestCommand("/teammarker list ")
                    )
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
            val cmds = mutableListOf("set", "remove", "list", "help")
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