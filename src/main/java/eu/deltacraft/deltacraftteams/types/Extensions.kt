package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.utils.enums.Settings
import org.bukkit.configuration.file.FileConfiguration
import java.sql.Connection
import java.sql.DriverManager


fun FileConfiguration.getString(setting: Settings): String? {
    return this.getString(setting.path)
}