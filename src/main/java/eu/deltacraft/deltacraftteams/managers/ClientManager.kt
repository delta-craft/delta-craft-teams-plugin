package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.interfaces.IConfigConsumer
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.PointsResult
import eu.deltacraft.deltacraftteams.types.getString
import eu.deltacraft.deltacraftteams.utils.enums.PointsError
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.bukkit.configuration.file.FileConfiguration

class ClientManager(plugin: DeltaCraftTeams) : IConfigConsumer {

    private val logged = plugin.logger
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

    suspend fun uploadPoints(points: List<Point>): PointsResult {
        val client = this.getClient()

        val httpRes = client.post<HttpResponse>(path = "api/plugin/addpoints") {
            contentType(ContentType.Application.Json)
            body = points
        }

        client.close()

        val status = httpRes.status

        if (status != HttpStatusCode.OK && status != HttpStatusCode.BadRequest) {
            return PointsResult(false, PointsError.Unknown.value, "HTTP status: $status")
        }

        return httpRes.receive()
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