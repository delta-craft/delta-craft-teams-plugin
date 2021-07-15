package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.interfaces.IConfigConsumer
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.getString
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.request.*
import io.ktor.http.*
import org.bukkit.configuration.file.FileConfiguration

class ClientManager(plugin: DeltaCraftTeams) : IConfigConsumer {

    override val config: FileConfiguration = plugin.config

    private var apiKey: String? = null

    fun getClient(): HttpClient {
        return HttpClient(Java) {
            install(JsonFeature) {
                accept(ContentType.Application.Json)
                serializer = GsonSerializer()
            }
            install(UserAgent) {
                agent = "DeltaCraftTeams plugin"
            }
            defaultRequest {
                host = Constants.BASE_URL
                url {
                    protocol = URLProtocol.HTTPS
                }
                header("authorization", apiKey)
            }
            expectSuccess = false
        }
    }


    override fun onConfigReload() {
        reloadApiKey()
    }

    private fun reloadApiKey() {
        apiKey = internalGetApiKey()
    }

    private fun internalGetApiKey(): String? {
        return config.getString(Settings.APIKEY)
    }

    init {
        reloadApiKey()
    }
}