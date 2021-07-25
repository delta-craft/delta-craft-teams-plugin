package eu.deltacraft.deltacraftteams.managers.bluemap

import eu.deltacraft.deltacraftteams.types.Team
import eu.deltacraft.deltacraftteams.types.TeamMarker
import eu.deltacraft.deltacraftteams.types.toVector3d

class BlueMapTeamMarkerIntegration : BlueMapIntegrationBase() {
    companion object {

        private const val MARKERS_KEY: String = "Team markers"

        fun addMarker(team: Team, teamMarker: TeamMarker): Boolean {
            val location = teamMarker.location

            val (markerApi, map) = getMarkerWithMap(location) ?: return false

            val markers = markerApi.createMarkerSet(getMarkerSetId(team))
            markers.label = getMarkerSetLabel(team.name)

            val marker = markers.createPOIMarker(teamMarker.name, map, location.toVector3d())
            marker.label = teamMarker.name

            // marker.setIcon("${Constants.FULL_URL}/api/embed/teammarker/${teamMarker.id}", 16, 16)

            markerApi.save()
            return true
        }

        fun removeMarker(team: Team, teamMarker: TeamMarker) {
            removeMarker(team.id, teamMarker.name)
        }

        fun removeMarker(teamId: Int, teamName: String) {
            val markerApi = getMarkerApi() ?: return

            val markers = markerApi.createMarkerSet(getMarkerSetId(teamId))

            markers.removeMarker(teamName)

            markerApi.save()
        }

        private fun getMarkerSetLabel(name: String): String {
            return "Team markers: $name"
        }

        private fun getMarkerSetId(team: Team): String {
            return getMarkerSetId(team.id)
        }

        private fun getMarkerSetId(teamId: Int): String {
            return "Team_$teamId"
        }

    }
}