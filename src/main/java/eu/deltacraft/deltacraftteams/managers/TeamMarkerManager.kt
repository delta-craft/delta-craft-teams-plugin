package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.bluemap.BlueMapTeamMarkerIntegration
import eu.deltacraft.deltacraftteams.managers.cache.TeamCacheManager
import eu.deltacraft.deltacraftteams.managers.cache.TeamMarkerCache
import eu.deltacraft.deltacraftteams.managers.cache.TeamOwnerManager
import eu.deltacraft.deltacraftteams.managers.templates.CacheConfigManager
import eu.deltacraft.deltacraftteams.types.IsTeamOwnerResponse
import eu.deltacraft.deltacraftteams.types.TeamMarker
import eu.deltacraft.deltacraftteams.types.getInt
import eu.deltacraft.deltacraftteams.types.hasPermission
import eu.deltacraft.deltacraftteams.utils.TextHelper
import eu.deltacraft.deltacraftteams.utils.enums.Permissions
import eu.deltacraft.deltacraftteams.utils.enums.Settings
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class TeamMarkerManager(
    plugin: DeltaCraftTeams,
    markerCache: TeamMarkerCache,
    private val teamsCache: TeamCacheManager,
    private val clientManager: ClientManager,
    private val teamOwnerManager: TeamOwnerManager,
) :
    CacheConfigManager<TeamMarkerCache>(plugin, "teammarkers.yml", markerCache) {

    constructor(plugin: DeltaCraftTeams, clientManager: ClientManager, mainManager: DeltaCraftTeamsManager)
            : this(plugin,
        mainManager.teamMarkerCache,
        mainManager.teamCacheManager,
        clientManager,
        mainManager.teamOwnerManager)


    override fun loadCache() {
        val marksers = getMarksers()
        cacheManager.load(marksers)
    }

    private fun getMarksers(): Map<String, TeamMarker> {
        val keys = config.getKeys(false)
        if (keys.isEmpty()) {
            return emptyMap()
        }
        val res = mutableMapOf<String, TeamMarker>()
        for (idString in keys) {
            val marker = getMarker(idString)
            if (marker != null) {
                res[idString] = marker
            }
        }
        return res
    }

    private fun getMarker(id: String): TeamMarker? {
        return config.getSerializable(id, TeamMarker::class.java, null)
    }

    fun getTeamMarkers(player: Player): List<TeamMarker> {
        val team = teamsCache[player]
        if (team == null) {
            player.sendMessage(TextHelper.attentionText("You have no team"))
            return listOf()
        }
        return getTeamMarkers(team.id)
    }

    private fun getTeamMarkers(teamId: Int): List<TeamMarker> {
        return cacheManager.values.filter { x -> x.teamId == teamId }
    }

    private fun getTeamMarkersCount(teamId: Int): Int {
        return getTeamMarkers(teamId).size
    }

    fun getAllMarkers(): Collection<TeamMarker> {
        return cacheManager.values
    }

    fun setMarker(p: Player, name: String) {
        val location = p.location
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {
                setMarkerAsync(p, location, name)
            }
        })
        p.sendMessage(TextHelper.infoText("Checking if you are an owner..."))
    }

    private suspend fun setMarkerAsync(p: Player, location: Location, name: String) {
        val isOwner = checkIfIsOwnerAsync(p.uniqueId)
        if (!isOwner) {
            p.sendMessage(TextHelper.attentionText("You are not an team owner", NamedTextColor.RED))
            return
        }
        createMarker(p, location, name)
    }

    private fun createMarker(p: Player, location: Location, name: String) {
        val team = teamsCache[p] ?: return

        val max = getMaxTeamMarkersCount()
        val currentCount = getTeamMarkersCount(team.id)
        if (currentCount >= max) {
            p.sendMessage(TextHelper.attentionText("Max team markers count reached"))
            return
        }

        val marker = TeamMarker(name, team.id, location)
        val id = marker.id.toString()

        val res = BlueMapTeamMarkerIntegration.addMarker(team, marker)
        if (!res) {
            p.sendMessage(TextHelper.attentionText("Error while adding marker to BlueMap"))
            return
        }

        config.set(id, marker)
        saveConfig()

        cacheManager[id] = marker

        p.sendMessage(TextHelper.infoText("Marker created", NamedTextColor.GREEN))
    }

    fun deleteMarker(p: Player, id: String) {
        if (p.hasPermission(Permissions.TEAMMARKERADMIN)) {
            removeMarker(p, id)
            return
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {
                deleteMarkerAsync(p, id)
            }
        })
        p.sendMessage(TextHelper.infoText("Checking if you are an owner..."))
    }

    private suspend fun deleteMarkerAsync(p: Player, id: String) {
        val isOwner = checkIfIsOwnerAsync(p.uniqueId)
        if (!isOwner) {
            p.sendMessage(TextHelper.attentionText("You are not an team owner", NamedTextColor.RED))
            return
        }
        removeMarker(p, id)
    }

    private fun removeMarker(p: Player, id: String) {
        val marker = cacheManager[id]
        if (marker == null) {
            p.sendMessage(TextHelper.attentionText("Marker with given id not found"))
            return
        }

        val res = BlueMapTeamMarkerIntegration.removeMarker(marker)
        if (!res) {
            p.sendMessage(TextHelper.attentionText("Error while deleting marker from BlueMap"))
            return
        }

        config[id] = null
        saveConfig()

        cacheManager.remove(id)

        p.sendMessage(TextHelper.infoText("Marker deleted", NamedTextColor.GREEN))
    }

    private suspend fun checkIfIsOwnerAsync(uuid: UUID): Boolean {
        val cache = teamOwnerManager[uuid]
        if (cache != null) {
            return cache.isOwner
        }
        val client = clientManager.getClient()

        val httpRes = client.get<HttpResponse>(path = "team/is-owner") {
            parameter("uuid", uuid.toString())
        }

        client.close()

        val status = httpRes.status

        if (status != HttpStatusCode.OK) {
            return false
        }

        val res = httpRes.receive<IsTeamOwnerResponse>()

        return teamOwnerManager.set(uuid, res.content).isOwner
    }

    private fun getMaxTeamMarkersCount(): Int {
        return plugin.config.getInt(Settings.MAXTEAMMARKERS)
    }
}