package eu.deltacraft.deltacraftteams.managers.bluemap

import eu.deltacraft.deltacraftteams.types.Constants
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

            val marker = markers.createPOIMarker(teamMarker.id.toString(), map, location.toVector3d())
            marker.label = teamMarker.name

            marker.setIcon("${Constants.API_FULL_URL}/embed/teammarker/${teamMarker.teamId}", 16, 16)

            markerApi.save()
            return true
        }

        fun removeMarker(teamMarker: TeamMarker): Boolean {
            return removeMarker(teamMarker.teamId, teamMarker.id.toString())
        }

        private fun removeMarker(teamId: Int, markerId: String): Boolean {
            val markerApi = getMarkerApi() ?: return false

            val markers = markerApi.createMarkerSet(getMarkerSetId(teamId))

            markers.removeMarker(markerId)

            markerApi.save()
            return true
        }

        private fun getMarkerSetLabel(name: String): String {
            return "Team '$name' markers"
        }

        private fun getMarkerSetId(team: Team): String {
            return getMarkerSetId(team.id)
        }

        private fun getMarkerSetId(teamId: Int): String {
            return "Team_$teamId"
        }

    }
}