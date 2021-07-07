package eu.deltacraft.deltacraftteams.managers.templates

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import io.sentry.Sentry
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Level


abstract class ConfigManager(protected val plugin: DeltaCraftTeams, private val fileName: String) {
    protected lateinit var config: YamlConfiguration
    private val configFile: File = File(plugin.dataFolder, fileName)

    init {
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false)
        }
        lazy { reloadAll() }
    }


    open fun reloadAll() {
        config = YamlConfiguration.loadConfiguration(configFile)

        // Look for defaults in the jar
        val defaultStream = plugin.getResource(fileName)
        if (defaultStream != null) {
            val defConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            config.setDefaults(defConfig)
        }
    }

    fun saveConfig() {
        try {
            config.save(configFile)
        } catch (ex: IOException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save config to $configFile", ex)
            Sentry.captureException(ex)
        }
    }

    fun setLocation(path: String, l: Location) {
        config[path] = l
    }

    fun getLocation(path: String): Location? {
        return if (!config.contains(path)) null else config[path] as Location?
    }
}
