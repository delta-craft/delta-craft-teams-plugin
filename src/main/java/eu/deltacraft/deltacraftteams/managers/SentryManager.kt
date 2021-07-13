package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.interfaces.IConfigConsumer
import eu.deltacraft.deltacraftteams.types.getBoolean
import eu.deltacraft.deltacraftteams.types.getString
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.sentry.Sentry
import org.bukkit.configuration.file.FileConfiguration

class SentryManager(private val plugin: DeltaCraftTeams) : IConfigConsumer {
    override val config: FileConfiguration = plugin.config

    fun tryInitSentry() {
        val isDebug: Boolean = config.getBoolean(Settings.DEBUG)

        val dsn = config.getString(Settings.SENTRY)
        if (dsn.isNullOrEmpty()) {
            return
        }

        try {
            Sentry.init { x ->
                run {
                    x.dsn = dsn
                    x.tracesSampleRate = 1.0
                    x.setDebug(isDebug)
                }
            }
            plugin.debugMsg("Sentry enabled")
        } catch (e: Exception) {
        }
    }

    override fun onConfigReload() {
        tryInitSentry()
    }


}