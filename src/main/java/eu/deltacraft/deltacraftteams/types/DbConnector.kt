package eu.deltacraft.deltacraftteams.types

import eu.deltacraft.deltacraftteams.interfaces.IConfigConsumer
import eu.deltacraft.deltacraftteams.interfaces.IDbConnector
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import org.bukkit.configuration.file.FileConfiguration

class DbConnector(override val config: FileConfiguration) : IDbConnector, IConfigConsumer {
    init {
        onConfigReload()
    }

    private lateinit var connectionString: ConnectionString;

    override fun getConnectionString(): ConnectionString {
        return connectionString
    }

    override fun onConfigReload() {
        val host = getHostString(config)
        val loginName = config.getString(Settings.DATABASELOGIN) ?: ""
        val pwd = config.getString(Settings.DATABASEPASSWORD) ?: ""

        connectionString = ConnectionString(host, loginName, pwd)
    }

    private fun getHostString(config: FileConfiguration): String {
        var server = config.getString(Settings.DATABASESERVER) ?: return ""

        if (!server.contains(':')) {
            server += ":3306"
        }

        val dbName = config.getString(Settings.DATABASENAME) ?: return ""

        return "jdbc:mysql://$server/$dbName"
    }
}
