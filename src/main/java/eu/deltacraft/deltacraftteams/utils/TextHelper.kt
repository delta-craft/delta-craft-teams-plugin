package eu.deltacraft.deltacraftteams.utils

import eu.deltacraft.deltacraftteams.types.Constants
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
                .append(Component.text(divider, NamedTextColor.GRAY))
                .append(Component.newline())
        }

        @JvmStatic
        fun createActionButton(
            button: Component,
            color: TextColor = NamedTextColor.DARK_AQUA,
        ): Component {
            return Component.text("[ ", NamedTextColor.WHITE, TextDecoration.BOLD)
                .append(button).color(color).decorate(TextDecoration.BOLD)
                .append(Component.text(" ]", NamedTextColor.WHITE, TextDecoration.BOLD))
        }

        @JvmStatic
        fun insufficientPermissions(permission: Permissions): TextComponent {
            return this.insufficientPermissions("Insufficient permissions!", permission.path)
        }

        @JvmStatic
        fun insufficientPermissions(
            customMsg: String = "Insufficient permissions!",
            permission: String = "¯\\_(ツ)_/¯",
        ): TextComponent {
            return Component
                .text(customMsg, NamedTextColor.DARK_RED)
                .hoverEvent(
                    HoverEvent.showText(
                        Component.text("Missing permission: '$permission'")
                    )
                )
        }

        @JvmStatic
        fun getPrefix(): TextComponent {
            return Component.empty()
                .append(
                    Component.text("[DELTACRAFT] ")
                        .color(NamedTextColor.GOLD)
                        .clickEvent(
                            ClickEvent.openUrl(Constants.FULL_URL)
                        )
                        .hoverEvent(
                            HoverEvent.showText(
                                Component.text("Click to open portal")
                            )
                        )
                )
        }

        @JvmStatic
        fun infoText(
            text: String,
            color: TextColor = NamedTextColor.YELLOW,
            decoration: TextDecoration? = null,
        ): TextComponent {

            val textComponent =
                if (decoration == null) Component.text(text, color) else Component.text(text, color, decoration)

            return getPrefix()
                .append(
                    textComponent
                )
        }

        @JvmStatic
        fun attentionText(text: String, color: TextColor = NamedTextColor.YELLOW): Component {
            return infoText(text, color, TextDecoration.BOLD)
        }

        @JvmStatic
        fun varText(text: String): TextComponent {
            return Component.text(text, NamedTextColor.WHITE)
        }

        @JvmStatic
        fun commandInfo(fullCommand: String, description: String, command: String = fullCommand): TextComponent {
            return Component.empty()
                .append(
                    Component.text("$fullCommand ", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.suggestCommand(command))
                ).append(
                    Component.text(description, NamedTextColor.GREEN)
                ).append(Component.newline())
        }

        @JvmStatic
        fun visitUrl(what: String, urlPath: String): TextComponent {
            return Component.text("Visit ")
                .append(Component.text("DeltaCraft Portal ", NamedTextColor.DARK_AQUA))
                .append(Component.text("and ${what}."))
                .append(Component.newline())
                .append(Component.newline())
                .append(
                    Component
                        .text("${Constants.FULL_URL}/${urlPath}", NamedTextColor.DARK_AQUA)
                        .clickEvent(
                            ClickEvent.openUrl("${Constants.FULL_URL}/${urlPath}")
                        )
                )
        }
    }
}