package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.utils.TextHelper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LinksCommand : CommandExecutor {

    private fun getTitle(text: String): TextComponent {
        return Component.empty()
            .append(
                Component.text("[", NamedTextColor.DARK_GRAY)
            ).append(Component.text(text, NamedTextColor.DARK_AQUA, TextDecoration.BOLD))
            .append(
                Component.text("]", NamedTextColor.DARK_GRAY)
            )
    }

    private fun getLink(link: String): TextComponent {
        return Component.empty()
            .append(
                Component.text(" - $link")
                    .clickEvent(ClickEvent.openUrl(link))
            )
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You already know the links")
            return false
        }

        val player: Player = sender

        val c = Component.empty()
            .append(TextHelper.getDivider())
            .append(Component.newline())
            .append(
                getTitle("PORTAL")
            ).append(
                getLink("https://portal.deltacraft.eu")
            )
            .append(Component.newline())
            .append(
                getTitle("MAP")
            ).append(
                getLink("https://map.deltacraft.eu")
            )
            .append(Component.newline())
            .append(
                getTitle("SUPPORT")
            ).append(
                getLink("https://portal.deltacraft.eu/support")
            )
            .append(Component.newline())
            .append(
                getTitle("DISCORD")
            ).append(
                getLink("https://portal.deltacraft.eu/discord")
            )
            .append(Component.newline())
            .append(TextHelper.getDivider())

        player.sendMessage(c)
        return true
    }
}