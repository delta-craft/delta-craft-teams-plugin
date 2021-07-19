package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.types.Constants
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DonationCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Why do you need to donate from console")
            return false
        }

        val p: Player = sender

        p.sendMessage(
            Component.text("Donation link ")
                .append(
                    Component
                        .text("${Constants.FULL_URL}/donate", NamedTextColor.DARK_AQUA)
                        .clickEvent(
                            ClickEvent.openUrl("${Constants.FULL_URL}/donate")
                        )
                )
        )

        return true
    }
}