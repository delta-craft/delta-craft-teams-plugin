package eu.deltacraft.deltacraftteams.utils

import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

class TextHelper {
    companion object {
        @JvmStatic
        fun getDivider(): TextComponent {
            val divider = "===================================="
            return Component.newline()
                .append(Component.text(divider).color(NamedTextColor.GRAY))
                .append(Component.newline())
        }

        @JvmStatic
        fun createActionButton(
            button: Component,
            color: TextColor = NamedTextColor.DARK_AQUA
        ): Component {
            return Component.text("[ ").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD)
                .append(button).color(color).decorate(TextDecoration.BOLD)
                .append(Component.text(" ]").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD))
        }

        @JvmStatic
        fun insufficientPermissions(permission: Permissions): TextComponent {
            return this.insufficientPermissions("Insufficient permissions!", permission.path)
        }

        @JvmStatic
        fun insufficientPermissions(
            customMsg: String = "Insufficient permissions!",
            permission: String = "¯\\_(ツ)_/¯"
        ): TextComponent {
            return Component
                .text(customMsg)
                .color(NamedTextColor.DARK_RED)
                .hoverEvent(
                    HoverEvent.showText(
                        Component.text("Missing permission: '$permission'")
                    )
                )
        }

        @JvmStatic
        fun infoText(text: String): TextComponent {
            return this.infoText(text, NamedTextColor.YELLOW)
        }

        @JvmStatic
        fun infoText(text: String, color: TextColor? = NamedTextColor.YELLOW): TextComponent {
            return Component.text(text)
                .color(color)
        }

        @JvmStatic
        fun attentionText(text: String): Component {
            return infoText(text)
                .decorate(TextDecoration.BOLD)
        }

        @JvmStatic
        fun varText(text: String): TextComponent {
            return Component.text(text)
                .color(NamedTextColor.WHITE)
        }

        @JvmStatic
        fun commandInfo(fullCommand: String, description: String, command: String = fullCommand): TextComponent {
            return Component.empty()
                .append(
                    Component.text("$fullCommand ")
                        .color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.suggestCommand(command))
                ).append(
                    Component.text(description).color(NamedTextColor.GREEN)
                ).append(Component.newline())
        }
    }
}