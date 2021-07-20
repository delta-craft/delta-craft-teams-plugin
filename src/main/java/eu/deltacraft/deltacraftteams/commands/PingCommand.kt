package eu.deltacraft.deltacraftteams.commands

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PingCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            if (args.size != 1) {
                sender.sendMessage("Missing arg - player name")
                return false
            }

            val playerName = args.first()
            val pl = Bukkit.getPlayer(playerName)

            if (pl == null) {
                sender.sendMessage("$playerName není online")
            } else {
                sender.sendMessage("Pong - $playerName - ${pl.ping}ms")
            }
            return true

        }

        val p: Player = sender

        if (args.size == 1) {
            val playerName = args.first()
            val pl = Bukkit.getPlayer(playerName)

            if (pl == null) {
                p.sendMessage(Component.text("$playerName není online"))
            } else {
                p.sendMessage(Component.text("Pong - $playerName - ${pl.ping}ms"))
            }
            return true
        }

        p.sendMessage(Component.text("Pong - ${p.ping}ms"))
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
    }
}