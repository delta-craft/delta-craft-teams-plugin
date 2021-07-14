package eu.deltacraft.deltacraftteams.types

import com.flowpowered.math.vector.Vector3d
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.permissions.Permissible


fun FileConfiguration.getString(setting: Settings): String? {
    return this.getString(setting.path)
}

fun FileConfiguration.getBoolean(setting: Settings): Boolean {
    return this.getBoolean(setting.path)
}

fun FileConfiguration.getInt(setting: Settings): Int {
    return this.getInt(setting.path)
}

fun Permissible.hasPermission(perm: Permissions): Boolean {
    return this.hasPermission(perm.path)
}

fun Location.hasMoved(to: Location): Boolean {
    return this.blockX != to.blockX ||
            this.blockY != to.blockY ||
            this.blockZ != to.blockZ
}

fun PlayerMoveEvent.hasMoved(): Boolean {
    return this.from.hasMoved(this.to)
}

fun Location.toVector3d(): Vector3d {
    return Vector3d(this.x, this.y, this.z)
}
