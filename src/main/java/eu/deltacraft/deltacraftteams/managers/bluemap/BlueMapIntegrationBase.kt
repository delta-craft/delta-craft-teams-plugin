package eu.deltacraft.deltacraftteams.managers.bluemap

import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.BlueMapMap
import de.bluecolored.bluemap.api.marker.MarkerAPI
import org.bukkit.Location
import java.util.*

abstract class BlueMapIntegrationBase {

    companion object {
        fun getApi(): BlueMapAPI? {
            val optionalApi = BlueMapAPI.getInstance()
            if (optionalApi.isEmpty) {
                return null
            }
            return optionalApi.get()
        }

        fun getMarkerApi(): MarkerAPI? {
            val mapApi = getApi() ?: return null
            val markerApi = mapApi.markerAPI

            markerApi.load()

            return markerApi
        }

        fun getApiWithMarker(): Pair<BlueMapAPI, MarkerAPI>? {
            val mapApi = getApi() ?: return null
            val markerApi = mapApi.markerAPI

            markerApi.load()

            return Pair(mapApi, markerApi)
        }

        fun getMarkerWithMap(location: Location): Pair<MarkerAPI, BlueMapMap>? {
            return getMarkerWithMap(location.world.uid)
        }

        fun getMarkerWithMap(worldUid: UUID): Pair<MarkerAPI, BlueMapMap>? {
            val (api, markerApi) = getApiWithMarker() ?: return null

            val optionalWorld = api.getWorld(worldUid) ?: return null
            if (optionalWorld.isEmpty) {
                return null
            }

            val world = optionalWorld.get()

            val map = world.maps?.firstOrNull() ?: return null

            return Pair(markerApi, map)
        }
    }

}