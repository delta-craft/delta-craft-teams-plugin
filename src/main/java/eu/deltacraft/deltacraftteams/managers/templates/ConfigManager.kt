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

    private val configFile: File = File(plugin.dataFolder, fileName)

    protected lateinit var config: YamlConfiguration

    init {
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false)
        }

        reloadAll()
    }

    protected fun reloadAll() {
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

}
