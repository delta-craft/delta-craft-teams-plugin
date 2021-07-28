package eu.deltacraft.deltacraftteams.managers.bluemap

import de.bluecolored.bluemap.api.marker.DistanceRangedMarker
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.toVector3d
import org.bukkit.Location
import org.bukkit.entity.Player

class BlueMapHomeIntegration : BlueMapIntegrationBase() {
    companion object {
        private const val MARKERS_KEY: String = "Homes"

        fun addHome(player: Player, location: Location) {

            val (markerApi, map) = getMarkerWithMap(location) ?: return

            val homeName = getPlayerHomeName(player)

            val markers = markerApi.createMarkerSet(MARKERS_KEY)
            markers.label = "Homes"

            val marker = markers.createPOIMarker(player.name, map, location.toVector3d())
            marker.label = homeName
            marker.setIcon("${Constants.FULL_URL}/api/embed/home/${player.name}", 16, 16)

            if (marker is DistanceRangedMarker) {
                (marker as DistanceRangedMarker).maxDistance = 200.0
            }

            markerApi.save()
        }

        fun removeHome(player: Player) {
            val markerApi = getMarkerApi() ?: return

            val markers = markerApi.createMarkerSet(MARKERS_KEY)

            markers.removeMarker(player.name)

            markerApi.save()
        }

        private fun getPlayerHomeName(player: Player): String {
            return "${player.name}'s home"
        }
    }
}