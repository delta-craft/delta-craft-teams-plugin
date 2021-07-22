package eu.deltacraft.deltacraftteams.managers

import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.marker.DistanceRangedMarker
import de.bluecolored.bluemap.api.marker.Shape
import eu.deltacraft.deltacraftteams.types.Constants
import eu.deltacraft.deltacraftteams.types.PvpZone
import eu.deltacraft.deltacraftteams.types.toVector3d
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class BlueMapManager {
    companion object {
        const val HomesMarkesId: String = "Homes"
        const val PvpZoneMarkersId: String = "Pvp Zones"
    }

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

        val world = optionalWorld.get()

        val map = world.maps.first()

        markerApi.load()

        val markers = markerApi.createMarkerSet(PvpZoneMarkersId)

        val shape = Shape.createRect(maxX, maxZ, minX, minZ)

        val friendlyName = getZoneDetailName(name)

        val marker = markers.createShapeMarker(name, map, shape, mainY)
        marker.label = friendlyName

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

        val markers = markerApi.createMarkerSet(PvpZoneMarkersId)

        val success = markers.removeMarker(name)

        markerApi.save()

        return success
    }

    private fun getZoneDetailName(name: String): String {
        return "PVP zone: $name"
    }

    fun addHome(player: Player, location: Location) {
        val optionalApi = BlueMapAPI.getInstance()
        if (optionalApi.isEmpty) {
            return
        }
        val mapApi = optionalApi.get()
        val markerApi = mapApi.markerAPI

        val optionalWorld = mapApi.getWorld(location.world.uid)
        if (optionalWorld.isEmpty) {
            return
        }
        val world = optionalWorld.get()

        val map = world.maps.first()
        val homeName = getPlayerHomeName(player)

        markerApi.load()

        val markers = markerApi.createMarkerSet(HomesMarkesId)

        val marker = markers.createPOIMarker(player.name, map, location.toVector3d())
        marker.label = homeName
        marker.setIcon("${Constants.FULL_URL}/api/embed/home/${player.name}", 16, 16)
        if (marker is DistanceRangedMarker) {
            (marker as DistanceRangedMarker).maxDistance = 200.0
        }

        markerApi.save()
    }

    fun removeHome(player: Player) {
        val optionalApi = BlueMapAPI.getInstance()
        if (optionalApi.isEmpty) {
            return
        }
        val mapApi = optionalApi.get()
        val markerApi = mapApi.markerAPI

        markerApi.load()

        val markers = markerApi.createMarkerSet(HomesMarkesId)

        markers.removeMarker(player.name)

        markerApi.save()
    }

    private fun getPlayerHomeName(player: Player): String {
        return "${player.name}'s home"
    }
}
