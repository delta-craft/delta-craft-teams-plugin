package eu.deltacraft.deltacraftteams.commands.home

import eu.deltacraft.deltacraftteams.managers.HomesManager
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetHomeCommand(private val configManager: HomesManager) : CommandExecutor {

    private val overrideString: String = "::override::"

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (commandSender !is Player) {
            commandSender.sendMessage("Only players can use this command")
            return true
        }

        val player: Player = commandSender

        if (!player.hasPermission(Permissions.HOME)) {
            player.sendMessage(TextHelper.insufficientPermissions(Permissions.HOME))
            return true
        }

        if (args.size > 1) {
            player.sendMessage("Correct usage of this command is /sethome <name>")
            return true
        }

        if (args.isNotEmpty() &&
            args[0].lowercase() == overrideString
        ) {
            player.sendMessage("Home name is invalid")
            return true
        }

        configManager.setHome(player)
        this.sendSuccess(player)
        return true
    }

    private fun sendSuccess(player: Player) {
        player.sendMessage(TextHelper.infoText("Home has been saved successfully!"))
        player.location.world?.spawnParticle(
            Particle.HEART,
            player.location.add((player.location.direction.multiply(2))).add(0.0, 1.0, 0.0),
            1
        )
    }

}