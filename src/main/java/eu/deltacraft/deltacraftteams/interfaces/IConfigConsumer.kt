package eu.deltacraft.deltacraftteams.interfaces

import org.bukkit.configuration.file.FileConfiguration

interface IConfigConsumer {
    val config: FileConfiguration

    fun onConfigReload()

}