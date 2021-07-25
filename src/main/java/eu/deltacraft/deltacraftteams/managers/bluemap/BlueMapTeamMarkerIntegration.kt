package eu.deltacraft.deltacraftteams.managers.bluemap

import org.bukkit.Location
import org.bukkit.entity.Player

class BlueMapTeamMarkerIntegration : BlueMapIntegrationBase() {
    companion object {

        private const val MARKERS_KEY: String = "Team markers"

        fun addHome(player: Player, location: Location, name: String): Boolean {
            val mapApi = getApi() ?: return false

            val markerApi = mapApi.markerAPI

            val optionalWorld = mapApi.getWorld(location.world.uid)
            if (optionalWorld.isEmpty) {
                return false
            }
            val world = optionalWorld.get()

            val map = world.maps.first()
            val id = getMarkerName(player, name)

            markerApi.load()

            val markers = markerApi.createMarkerSet(MARKERS_KEY)

/*
            val marker = markers.createPOIMarker(player.name, map, location.toVector3d())
            marker.label = homeName
            marker.setIcon("${Constants.FULL_URL}/api/embed/home/${player.name}", 16, 16)
            if (marker is DistanceRangedMarker) {
                (marker as DistanceRangedMarker).maxDistance = 200.0
            }
*/
            markerApi.save()
            return true
        }

        private fun getMarkerName(player: Player, name: String): String {
            return "${player.name}'s home"
        }
    }
}