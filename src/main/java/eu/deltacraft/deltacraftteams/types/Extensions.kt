package eu.deltacraft.deltacraftteams.types

import com.flowpowered.math.vector.Vector3d
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.ktor.http.*
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.permissions.Permissible


fun FileConfiguration.getString(setting: Settings): String? {
    return this.getString(setting.path)
}

fun FileConfiguration.getInt(setting: Settings): Int {
    return this.getInt(setting.path)
}

fun Permissible.hasPermission(perm: Permissions): Boolean {
    return this.hasPermission(perm.path)
}

fun Location.toVector3d(): Vector3d {
    return Vector3d(this.x, this.y, this.z)
}

fun Location.toWorldLocation(): Location {
    return this.clone().set(this.x * 8, this.y * 8, this.z * 8)
}

fun AsyncPlayerPreLoginEvent.disallow(status: HttpStatusCode) {
    this.disallow(
        AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
        Component.text("Server error. HTTP $status")
    )
}
