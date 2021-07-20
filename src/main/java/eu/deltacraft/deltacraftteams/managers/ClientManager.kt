package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.getString
import eu.deltacraft.deltacraftteams.types.responses.PointsResult
import eu.deltacraft.deltacraftteams.utils.enums.PointsError
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ClientManager(plugin: DeltaCraftTeams) {

    private val logged = plugin.logger

    private val apiKey: String = plugin.config.getString(Settings.APIKEY) ?: ""

    fun getClient(): HttpClient {
        return HttpClient(Java) {
            install(JsonFeature) {
                accept(ContentType.Application.Json)
                serializer = KotlinxSerializer()
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

    suspend fun uploadPoints(points: Collection<Point>): PointsResult {
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

}