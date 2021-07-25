package eu.deltacraft.deltacraftteams.managers.bluemap

import de.bluecolored.bluemap.api.marker.Shape
import eu.deltacraft.deltacraftteams.types.PvpZone
import java.util.*

class BlueMapPvpZoneIntegration : BlueMapIntegrationBase() {
    companion object {
        private const val MARKERS_KEY: String = "Pvp Zones"

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
            maxX: Int,
            maxZ: Int,
            minX: Int,
            minZ: Int,
            mainY: Int,
            worldUid: UUID,
            name: String,
        ): Boolean {
            val newMaxX = maxX + 1
            val newMaxZ = maxZ + 1

            return addZoneToMap(
                newMaxX.toDouble(),
                newMaxZ.toDouble(),
                minX.toDouble(),
                minZ.toDouble(),
                mainY.toFloat(),
                worldUid,
                name
            )
        }

        private fun addZoneToMap(
            maxX: Double,
            maxZ: Double,
            minX: Double,
            minZ: Double,
            mainY: Float,
            worldUid: UUID,
            name: String,
        ): Boolean {
            val (markerApi, map) = getMarkerWithMap(worldUid) ?: return false

            val markers = markerApi.createMarkerSet(MARKERS_KEY)

            val shape = Shape.createRect(maxX, maxZ, minX, minZ)

            val friendlyName = getZoneDetailName(name)

            val marker = markers.createShapeMarker(name, map, shape, mainY)
            marker.label = friendlyName

            markerApi.save()

            return true
        }

        fun removeZoneFromMap(name: String): Boolean {
            val markerApi = getMarkerApi() ?: return false

            val markers = markerApi.createMarkerSet(MARKERS_KEY)

            val success = markers.removeMarker(name)

            markerApi.save()

            return success
        }

        private fun getZoneDetailName(name: String): String {
            return "PVP zone: $name"
        }
    }
}