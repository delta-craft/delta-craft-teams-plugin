package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.permissions.Permissible


fun FileConfiguration.getString(setting: Settings): String? {
    return this.getString(setting.path)
}


fun Permissible.hasPermission(perm: Permissions): Boolean {
    return this.hasPermission(perm.path)
}