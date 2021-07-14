package eu.deltacraft.deltacraftteams.managers

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.cache.PlayerHomeCache
import eu.deltacraft.deltacraftteams.managers.templates.ConfigManager
import eu.deltacraft.deltacraftteams.types.KeyHelper
import eu.deltacraft.deltacraftteams.types.PlayerHome
import eu.deltacraft.deltacraftteams.utils.TextHelper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.math.floor

class HomesManager(plugin: DeltaCraftTeams) : ConfigManager(plugin, "home.yml") {
    val homesCache = PlayerHomeCache()
    private val mapManager = BlueMapManager()

    companion object {
        const val HomesPrefix = "homes"
    }

    private fun getKeyHelper(uid: UUID): KeyHelper {
        return KeyHelper(uid, HomesPrefix)
    }

    fun getHome(p: Player): PlayerHome? {
        val uid = p.uniqueId
        val kh = getKeyHelper(uid)

        if (!config.contains(kh.key))
            return null

        val location = config.getLocation(kh.key) ?: return null

        return PlayerHome(uid, location)
    }

    fun hasHome(p: Player): Boolean {
        return getHome(p) != null
    }

    fun setHome(p: Player) {
        this.setHome(p.uniqueId, p.location)
        mapManager.addHome(p, p.location)
    }

    private fun setHome(playerId: UUID, location: Location) {
        location.x = floor(location.x)
        location.z = floor(location.z)
        val centred = location.add(0.5, 0.0, 0.5)
        val pl = PlayerHome(playerId, centred)

        val kh = getKeyHelper(playerId)

        config[kh.key] = pl.location

        saveConfig()
    }

    fun delHome(p: Player): Pair<Boolean, Location?> {
        val kh = getKeyHelper(p.uniqueId)

        val home = getHome(p) ?: return Pair(false, null)

        val location = home.location.clone()

        config[kh.key] = null
        saveConfig()

        mapManager.removeHome(p)
        return Pair(true, location)
    }

    private fun isLava(block: Block): Boolean {
        return block.type == Material.LAVA || block.type == Material.LAVA_BUCKET
    }

    private fun isWater(block: Block): Boolean {
        return block.type == Material.LAVA || block.type == Material.LAVA_BUCKET
    }

    fun isObstructed(location: Location): Pair<Boolean, Component> {
        val blockUnder = location.block.getRelative(BlockFace.DOWN)
        if (blockUnder.isEmpty) {
            return Pair(true, TextHelper.attentionText("A block is missing under the home location!"))
        }

        if (isLava(blockUnder)) {
            return Pair(true, TextHelper.attentionText("There is a lava under the home position"))
        }

        val block = location.block
        val up = block.getRelative(BlockFace.UP)

        if (up.isLiquid == block.isLiquid) {
            if (isLava(up) || isLava(block)) {
                return Pair(true, TextHelper.attentionText("There is lava in the home position"))
            }

            if (isWater(up) || isWater(block)) {
                return Pair(true, TextHelper.attentionText("There is water in the home position"))
            }
        }

        if (up.isPassable && block.isPassable) {
            return Pair(false, Component.empty())
        }

        return Pair(
            true,
            TextHelper.attentionText("Home location is obstructed")
                .hoverEvent(
                    HoverEvent.showText(
                        Component.text("Obstructed by: '${block.type} and ${up.type}'")
                    )
                )
        )
    }
}
