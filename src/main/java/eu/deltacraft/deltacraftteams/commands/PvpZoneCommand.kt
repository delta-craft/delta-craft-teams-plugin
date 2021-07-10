package eu.deltacraft.deltacraftteams.commands

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.PvpZoneManager
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PvpZoneCommand(
    private val plugin: DeltaCraftTeams,
    private val manager: PvpZoneManager
) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this commands")
            return false
        }
        val p: Player = sender

        if (!p.hasPermission(Permissions.PVPCREATE)) {
            p.sendMessage(TextHelper.insufficientPermissions(Permissions.PVPCREATE))
            return true
        }

        if (args.isEmpty() || args[0].isEmpty()) {
            p.sendMessage(ChatColor.GREEN.toString() + "Use " + ChatColor.YELLOW + "/pvp ? " + ChatColor.GREEN + "for help")
            return true
        }
        val cmd = args[0].trim()

        if (cmd.equals("test", true)) {
            this.isInPvpZone(p)
            return true
        }

        if (args.size < 2) {
            if (cmd.equals("?", true) || cmd.equals("help", true)) {
                this.sendHelp(p)
                return true
            }
            p.sendMessage(ChatColor.YELLOW.toString() + "You must pass some arguments")
            return true
        }

        val arg = args[1].trim()
        if (cmd.equals("set", true)) {
            when (arg) {
                "1", "one", "first" -> this.setTempLocation(p, true)
                "2", "two", "second" -> this.setTempLocation(p, false)
                else -> {
                    p.sendMessage(ChatColor.YELLOW.toString() + "You can only set point '1' or '2'")
                    return true
                }
            }
            if (this.pointsAreSet(p)) {
                p.sendMessage(ChatColor.GREEN.toString() + "Well done! You can now create pvp zone by " + ChatColor.YELLOW + "/pvp create <name of the zone>")
            }
            return true
        }

        if (cmd.equals("create", true)) {
            if (!p.hasPermission(Permissions.PVPCREATE)) {
                p.sendMessage(TextHelper.insufficientPermissions(Permissions.PVPCREATE))
                return true
            }
            this.createPvpZone(p, arg)
            return true
        }

        if (cmd.equals("delete", true) || cmd.equals("remove", true)) {
            if (!p.hasPermission(Permissions.PVPREMOVE)) {
                p.sendMessage(TextHelper.insufficientPermissions(Permissions.PVPREMOVE))
                return true
            }
            this.deletePvpZone(p, arg)
            return true
        }

        return true
    }

    private fun setTempLocation(p: Player, first: Boolean = true) {
        val loc = p.location
        if (manager.cacheManager.isInPvpZone(loc)) {
            p.sendMessage(TextHelper.infoText("This location is already in zone"))
            return
        }
        val pointName = if (first) 1 else 2

        manager.saveTempLocation(p.uniqueId, loc, first)
        p.sendMessage(ChatColor.GREEN.toString() + "Point " + ChatColor.YELLOW + pointName + ChatColor.GREEN + " saved")
    }

    private fun pointsAreSet(p: Player): Boolean {
        return manager.pointsAreSet(p.uniqueId)
    }

    private fun createPvpZone(p: Player, name: String) {
        val playerId = p.uniqueId

        if (manager.zoneExists(name)) {
            p.sendMessage(TextHelper.infoText("Zone with this name already exists"))
            return
        }
        val tempZone = manager.getTempZone(playerId)
        if (tempZone.first == null) {
            p.sendMessage(ChatColor.RED.toString() + "Point 1 is not set")
            return
        }
        if (tempZone.second == null) {
            p.sendMessage(ChatColor.RED.toString() + "Point 2 is not set")
            return
        }
        manager.addZone(tempZone.first!!, tempZone.second!!, name)
        manager.clearTempLocations(playerId)
        p.sendMessage(ChatColor.GREEN.toString() + "Zone " + ChatColor.YELLOW + name + ChatColor.GREEN + " successfully created")
    }

    private fun deletePvpZone(p: Player, name: String) {
        if (!manager.zoneExists(name)) {
            p.sendMessage(TextHelper.infoText("Zone with this name does not exists", NamedTextColor.RED))
            return
        }
        manager.removeZone(name)
        p.sendMessage(ChatColor.GREEN.toString() + "Zone " + ChatColor.YELLOW + name + ChatColor.GREEN + " successfully deleted")
        return
    }

    private fun sendHelp(p: Player) {
        val text = Component.text("PVP zones =========================")
            .append(Component.newline())
            .append(
                Component.text("/pvp set <1 or 2>", NamedTextColor.YELLOW)
            )
            .append(
                Component.text(" Set first and second point of a pvp zone", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(
                Component.text("/pvp create <name>", NamedTextColor.YELLOW)
            )
            .append(
                Component.text(" Create pvp zone", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(
                Component.text("/pvp remove <name>", NamedTextColor.YELLOW)
            )
            .append(
                Component.text(" Remove pvp zone", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(
                Component.text("/pvp test", NamedTextColor.YELLOW)
            )
            .append(
                Component.text(" Check if you are standing in a pvp zone", NamedTextColor.GREEN)
            )
            .append(Component.newline())
            .append(Component.text("===================================="))
        p.sendMessage(text)
    }

    private fun isInPvpZone(p: Player) {
        val l = p.location
        val reg = this.manager.cacheManager[l]
        var text = Component.text("You ")

        text = if (reg == null) {
            text.append(Component.text("are not ", NamedTextColor.RED))
        } else {
            text.append(Component.text("are ", NamedTextColor.GREEN))
        }

        text = text.append(Component.text("in a PVP zone ", NamedTextColor.WHITE))

        if (reg != null) {
            text = text.append(Component.text(reg.name, NamedTextColor.YELLOW))
        }

        p.sendMessage(text)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        if (sender !is Player) {
            return list
        }

        if (!command.name.equals("pvp", true)) {
            return list
        }

        if (args.size < 2 || args[0].isEmpty()) {
            var typedIn = ""
            if (args.size == 1) {
                typedIn = args[0].lowercase()
            }
            val cmds = arrayOf("set", "test", "create", "remove")
            for (cmd in cmds) {
                if (cmd.startsWith(typedIn)) {
                    list.add(cmd)
                }
            }
            return list
        }

        when (args[0].lowercase()) {
            "set" -> {
                list.add("1")
                list.add("2")
            }
            "remove" -> {
                var typedIn = ""
                if (args.size > 1) {
                    typedIn = args[1].lowercase()
                }
                val names: List<String> = this.manager.cacheManager.getPvpZoneNames()
                for (name in names) {
                    if (name.lowercase().startsWith(typedIn)) {
                        list.add(name)
                    }
                }
            }
        }

        return list
    }

}

