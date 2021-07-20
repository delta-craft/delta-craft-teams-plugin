package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.PointsQueue
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


class MainCommand(
    private val plugin: DeltaCraftTeams,
    private val pointsQueue: PointsQueue
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission(Permissions.USEMAIN)) {
            sender.sendMessage(TextHelper.insufficientPermissions(Permissions.USEMAIN))
            return true
        }
        if (args.isEmpty() || args[0].isBlank()) {
            val text = Component.text("Use ", NamedTextColor.GREEN)
                .append(
                    Component.text("/DeltaCraftTeams ? ", NamedTextColor.YELLOW)
                ).append(
                    Component.text("for help")
                )

            sender.sendMessage(text)
            return true
        }
        val cmd = args[0]
        if (cmd.equals("send", ignoreCase = true) ||
            cmd.equals("sendpoints", ignoreCase = true)
        ) {
            val res = pointsQueue.sendAllPoints()
            if (res) {
                sender.sendMessage(TextHelper.infoText("Prepared successfully"))
            } else {
                sender.sendMessage(TextHelper.attentionText("Send is already pending"))
            }
            return true
        }
        if (cmd.equals("version", ignoreCase = true)) {
            versionCommand(sender)
            return true
        }
        if (cmd.equals("help", ignoreCase = true) || cmd.equals("?", ignoreCase = true)) {
            sendHelp(sender)
            return true
        }
        sender.sendMessage("This is not a valid command")
        return true
    }

    private fun versionCommand(p: CommandSender) {
        val localVersion = plugin.description.version
        p.sendMessage(TextHelper.infoText("Current version: §a$localVersion", NamedTextColor.WHITE))
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        val list: MutableList<String> = ArrayList()
        if (sender !is Player) {
            return list
        }
        if (!sender.hasPermission(Permissions.USEMAIN)) {
            sender.sendMessage(TextHelper.insufficientPermissions(Permissions.USEMAIN))
            return list
        }
        val cmds = arrayOf("version", "send")
        val typedIn: String = if (args.size == 1) {
            args[0].lowercase()
        } else {
            return list
        }
        for (cmd in cmds) {
            if (cmd.startsWith(typedIn)) {
                list.add(cmd)
            }
        }
        return list
    }

    private fun sendHelp(p: CommandSender) {
        val text = Component.text("DeltaCraftTeams main commands =====================")
            .append(Component.newline())
            .append(TextHelper.commandInfo("/DeltaCraftTeams version", "Show current version of the plugin"))
            .append(TextHelper.commandInfo("/DeltaCraftTeams send", "Upload all points to database"))
            .append(
                Component.text("==================================================")
            )
        p.sendMessage(text)
    }

}