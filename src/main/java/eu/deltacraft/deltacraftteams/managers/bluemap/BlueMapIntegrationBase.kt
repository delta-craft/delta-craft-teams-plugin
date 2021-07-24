package eu.deltacraft.deltacraftteams.managers.bluemap

import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.marker.MarkerAPI

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
    }

}