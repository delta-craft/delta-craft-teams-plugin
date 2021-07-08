package eu.deltacraft.deltacraftteams.managers

import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.marker.Shape
import eu.deltacraft.deltacraftteams.types.PvpZone
import java.util.UUID

class BlueMapManager {
    fun addZoneToMap(zone: PvpZone): Boolean {
        return addZoneToMap(
            zone.maxX,
            zone.maxZ,
            zone.minX,
            zone.minZ,
            zone.mainY,
            zone.worldUniqueId,
            zone.name
        )
    }

    private fun addZoneToMap(
        xOne: Double,
        zOne: Double,
        xTwo: Double,
        zTwo: Double,
        mainY: Float,
        worldUid: UUID,
        name: String
    ): Boolean {
        val optionalApi = BlueMapAPI.getInstance()
        if (optionalApi.isEmpty) {
            return false
        }
        val mapApi = optionalApi.get()
        val markerApi = mapApi.markerAPI

        val optionalWorld = mapApi.getWorld(worldUid)
        if (optionalWorld.isEmpty) {
            return false
        }
        val markerSetName = getMarkerSetId(name)

        val world = optionalWorld.get()

        val map = world.maps.first()

        markerApi.load()

        val markers = markerApi.createMarkerSet(markerSetName)

        val shape = Shape.createRect(xOne, zOne, xTwo, zTwo)

        markers.createShapeMarker(markerSetName, map, shape, mainY)

        markerApi.save()

        return true
    }

    fun removeZoneFromMap(name: String): Boolean {
        val optionalApi = BlueMapAPI.getInstance()
        if (optionalApi.isEmpty) {
            return false
        }
        val mapApi = optionalApi.get()
        val markerApi = mapApi.markerAPI

        markerApi.load()

        val markerSetName = getMarkerSetId(name)

        val success = markerApi.removeMarkerSet(markerSetName)

        markerApi.save()

        return success
    }

    private fun getMarkerSetId(name: String): String {
        return "PVP zone: $name"
    }
}