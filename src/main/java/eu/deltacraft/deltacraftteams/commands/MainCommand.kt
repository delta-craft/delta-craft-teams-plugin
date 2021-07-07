package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import kotlin.collections.ArrayList


class MainCommand(private val plugin: DeltaCraftTeams) : CommandExecutor, TabCompleter {
    private fun getAllSettings(): List<Settings> {
        return Settings.values().filter { x -> x.visible }
    }

    private fun getAllSettingsKeys(): List<String> {
        return getAllSettings().map(Settings::path)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty() || args[0].isBlank()) {
            val text = Component.text("User ")
                .color(NamedTextColor.GREEN)
                .append(
                    Component.text("/DeltaCraftTeams ? ").color(NamedTextColor.YELLOW)
                ).append(
                    Component.text("for help")
                )

            sender.sendMessage(text)
            return true
        }
        val cmd = args[0]
        if (cmd.equals("reload", ignoreCase = true)) {
            if (!sender.hasPermission(Permissions.CONFIGRELOAD.path)) {
                sender.sendMessage(TextHelper.insufficientPermissions(Permissions.CONFIGRELOAD))
                return true
            }
            reloadConfig(sender)
            return true
        }
        if (cmd.equals("version", ignoreCase = true)) {
            if (!sender.hasPermission(Permissions.SHOWVERSION.path)) {
                sender.sendMessage(TextHelper.insufficientPermissions(Permissions.SHOWVERSION))
                return true
            }
            versionCommand(sender)
            return true
        }
        if (cmd.equals("help", ignoreCase = true) || cmd.equals("?", ignoreCase = true)) {
            sendHelp(sender)
            return true
        }
        if (cmd.equals("change", ignoreCase = true) || cmd.equals("set", ignoreCase = true)) {
            if (!sender.hasPermission(Permissions.CONFIGCHANGE.path)) {
                sender.sendMessage(TextHelper.insufficientPermissions(Permissions.CONFIGCHANGE))
                return true
            }
            if (args.size < 2 || args[1].isBlank()) {
                sender.sendMessage(TextHelper.attentionText("Key is empty"))
                return true
            }
            if (args.size < 3 || args[2].isBlank()) {
                sender.sendMessage(TextHelper.attentionText("Value is empty"))
                return true
            }
            val key = args[1]
            val newVal = args[2]
            changeConfig(sender, key, newVal)
            return true
        }
        if (cmd.equals("show", ignoreCase = true)) {
            if (!sender.hasPermission(Permissions.CONFIGSHOW.path)) {
                sender.sendMessage(TextHelper.insufficientPermissions(Permissions.CONFIGSHOW))
                return true
            }
            showCurrentSettings(sender)
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
        val cmds = arrayOf("show", "reload", "change", "version")
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
        var text = Component.text("DeltaCraftTeams main commands =====================")
            .append(Component.newline())
        if (p.hasPermission(Permissions.SHOWVERSION.path)) {
            text = text.append(TextHelper.commandInfo("/DeltaCraftTeams version", "Show current version of the plugin"))
        }
        if (p.hasPermission(Permissions.CONFIGSHOW.path)) {
            text = text.append(TextHelper.commandInfo("/DeltaCraftTeams show", "Show settings in config"))
        }
        if (p.hasPermission(Permissions.CONFIGCHANGE.path)) {
            text = text.append(
                TextHelper.commandInfo(
                    "/DeltaCraftTeams change <key> <value>",
                    "Change setting in config",
                    "/DeltaCraftTeams change "
                )
            )
        }
        if (p.hasPermission(Permissions.CONFIGRELOAD.path)) {
            text = text.append(TextHelper.commandInfo("/DeltaCraftTeams reload", "Reload plugin settings"))
        }
        text = text.append(
            Component.text("==================================================").color(NamedTextColor.WHITE)
        )
        p.sendMessage(text)
    }

    private fun reloadConfig(sender: CommandSender) {
        // TODO: Reload config
    }

    private fun changeConfig(p: CommandSender, key: String, value: String) {
        var configKey = key
        if (!configKey.startsWith("settings.") && !configKey.startsWith("system.")) {
            configKey = "settings.$configKey"
        }
        if (!getAllSettingsKeys().contains(configKey)) {
            p.sendMessage(TextHelper.attentionText("$configKey is not a valid config key "))
            return
        }
        var success = false

        // Boolean section
        if (configKey.equals(Settings.DEBUG.path, ignoreCase = true)) {
            val newVal = getBoolean(value)
            plugin.setDebug(newVal)
            success = true
        }
        if (success) {
            p.sendMessage("$configKey's value successfully changed to $value")
            return
        }
        // Number parsing
        val numberValue = value.toIntOrNull()
        if (numberValue == null) {
            p.sendMessage("$value is not a valid number")
            return
        }
        if (numberValue < 0) {
            p.sendMessage("Number cannot be negative number")
            return
        }

        // Number section
        /*  if (key.equals(Settings.SPECTATEMAXDISTANCE.path, ignoreCase = true)) {
              plugin.manager.getSpectateCacheManager().setMaxDistance(numberValue)
          }*/

        // Save config
        plugin.config.set(configKey, numberValue)
        plugin.saveConfig()
        p.sendMessage("$configKey's value successfully changed to $numberValue")
    }

    private fun showCurrentSettings(p: CommandSender) {
        p.sendMessage(TextHelper.getDivider())
        val config: FileConfiguration = plugin.config
        for (settings in getAllSettings()) {
            val key: String = settings.path
            val description: String = settings.description
            var value = config.getString(key)
            if (value == null || value.isBlank()) {
                value = "null"
            }
            var newVal = ""
            if (value.equals("true", ignoreCase = true)) {
                newVal = "false"
            }
            if (value.equals("false", ignoreCase = true)) {
                newVal = "true"
            }
            val toSend: Component = Component.empty()
                .append(
                    TextHelper.createActionButton(
                        Component.text(value)
                            .clickEvent(
                                ClickEvent.suggestCommand("/DeltaCraftTeams change $key $newVal")
                            )
                            .hoverEvent(
                                HoverEvent.showText(Component.text("Change value"))
                            ),
                        NamedTextColor.GREEN
                    )
                )
                .append(Component.text(" - "))
                .append(
                    Component.text(description)
                        .color(NamedTextColor.YELLOW)
                        .hoverEvent(
                            HoverEvent.showText(
                                Component.text(key.replace("settings.", ""))
                            )
                        )
                )
                .append(Component.text(" "))
                .append(Component.text(settings.getType()))

            p.sendMessage(toSend)
        }
        p.sendMessage(TextHelper.getDivider())
    }

    private fun getBoolean(value: String): Boolean {
        if (value.equals("on", ignoreCase = true)) {
            return true
        }
        return if (value.equals("1", ignoreCase = true)) {
            true
        } else java.lang.Boolean.parseBoolean(value)
    }

}