package eu.deltacraft.deltacraftteams.commands.home

import eu.deltacraft.deltacraftteams.managers.HomesManager
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class DelHomeCommand(private val homeConfigManager: HomesManager) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command")
            return true;
        }
        val player: Player = sender

        if (!player.hasPermission(Permissions.HOME)) {
            player.sendMessage(TextHelper.insufficientPermissions(Permissions.HOME))
            return true
        }

        val success = homeConfigManager.delHome(player)

        if (!success.first) {
            val output = Component.text("Home not found", NamedTextColor.YELLOW)
            player.sendMessage(output)
            return true
        }

        val output = Component.text("Home has been deleted!", NamedTextColor.YELLOW)
        player.sendMessage(output)

        val location = success.second
        val world = location?.world!!

        world.spawnParticle(Particle.VILLAGER_ANGRY, location.add(0.0, 0.5, 0.0), 1)
        world.playSound(location, Sound.BLOCK_CHAIN_BREAK, SoundCategory.MASTER, 2f, 1f)
        return true
    }
}