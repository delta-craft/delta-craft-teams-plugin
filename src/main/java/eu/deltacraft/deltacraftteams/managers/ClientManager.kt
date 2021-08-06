package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.Point
import eu.deltacraft.deltacraftteams.types.getString
import eu.deltacraft.deltacraftteams.types.points.MiningPoint
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
                serializer = KotlinxSerializer(
                    kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(UserAgent) {
                agent = "DeltaCraftTeams plugin"
            }
            defaultRequest {
                host = Constants.API_URL
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

        val finalPoints = points.toFinalPoints()

        val httpRes = client.post<HttpResponse>(path = "points/add") {
            contentType(ContentType.Application.Json)
            body = finalPoints
        }

        client.close()

        val status = httpRes.status

        if (status == HttpStatusCode.GatewayTimeout) {
            return PointsResult(true, null, "Timout but backend should take it")
        }

        if (status != HttpStatusCode.OK && status != HttpStatusCode.BadRequest) {
            return PointsResult(false, PointsError.Unknown.value, "HTTP status: $status")
        }

        return httpRes.receive()
    }

}

private fun Collection<Point>.toFinalPoints(): Collection<Point> {
    val res = mutableListOf<Point>()

    for (point in this) {
        if (point !is MiningPoint) {
            res.add(point)
            continue
        }
        val mining: MiningPoint = point
        val actualPoint = mining.toPoint()
        res.add(actualPoint)
    }

    return res
}


