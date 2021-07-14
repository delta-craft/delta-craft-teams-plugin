package eu.deltacraft.deltacraftteams.managers

import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.marker.DistanceRangedMarker
import de.bluecolored.bluemap.api.marker.Shape
import eu.deltacraft.deltacraftteams.types.PvpZone
import eu.deltacraft.deltacraftteams.types.toVector3d
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.UUID

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

        val world = optionalWorld.get()

        val map = world.maps.first()

        markerApi.load()

        val markers = markerApi.createMarkerSet(PvpZoneMarkersId)

        val shape = Shape.createRect(xOne, zOne, xTwo, zTwo)

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
        marker.setIcon("https://minotar.net/helm/${player.name}/30.png", 15, 15)
        (marker as DistanceRangedMarker).maxDistance = 200.0

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
