package eu.deltacraft.deltacraftteams.commands.home

import eu.deltacraft.deltacraftteams.managers.HomesManager
import eu.deltacraft.deltacraftteams.types.TeleportBar
import eu.deltacraft.deltacraftteams.types.getInt
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class HomeCommand(
    private val plugin: JavaPlugin,
    private val manager: HomesManager
) : CommandExecutor {

    private val cache = manager.homesCache
    private val overrideString: String = "::override::"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command")
            return false
        }
        val player: Player = sender

        if (!player.hasPermission(Permissions.HOME)) {
            player.sendMessage(TextHelper.insufficientPermissions(Permissions.HOME))
            return true
        }

        if (args.size > 1) {
            sender.sendMessage("Correct usage of this command is /home")
            return false
        }

        val overrideTp = args.isNotEmpty() && args[0].lowercase() == overrideString

        val home = manager.getHome(player)

        if (home == null) {
            val output = Component.text("Home not found", NamedTextColor.YELLOW)
            player.sendMessage(output)
            return true
        }

        val homeLocation = home.location

        val isObstructed = manager.isObstructed(homeLocation)

        if (isObstructed.first && !overrideTp) {
            val text =
                isObstructed.second
                    .append(Component.newline())
                    .append(Component.newline())
                    .append(
                        Component.text("TELEPORT ANYWAY")
                            .hoverEvent(
                                HoverEvent.showText(
                                    TextHelper.infoText("Proceed to teleport to anyway.")
                                )
                            )
                            .clickEvent(
                                ClickEvent.runCommand(
                                    "/home $overrideString"
                                )
                            )
                    )
            player.sendMessage(text)
            return true
        }

        if (cache.isTeleportPending(player)) {
            player.sendMessage(Component.text("You already have home teleportation pending"))
            return true
        }

        val delay = getDelay()

        val bossBar = TeleportBar(plugin)
        cache[player.uniqueId] = bossBar
        bossBar.showBar(player, delay)

        val taskId = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (cache.isTeleportPending(player)) {
                cache.cancelTeleport(player)

                player.teleport(homeLocation)
                player.sendMessage("Welcome home!")

                // Effects on teleport
                val world = player.location.world!!

                world.spawnParticle(Particle.EXPLOSION_NORMAL, player.location.add(0.0, 0.1, 0.0), 10)
                world.playSound(player.location, Sound.UI_TOAST_IN, SoundCategory.MASTER, 10f, 1f)
            }
        }, delay * 20L).taskId

        bossBar.mainTaskId = taskId

        return true
    }


    private fun getDelay(): Int {
        return plugin.config.getInt(Settings.HOMEDELAY)
    }

}
