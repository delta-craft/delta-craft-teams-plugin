package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.types.toWorldLocation
import eu.deltacraft.deltacraftteams.utils.TextHelper
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.plugin.java.JavaPlugin

class PortalListener(private val plugin: JavaPlugin) : Listener {

    @EventHandler
    fun onPortalCreate(event: PortalCreateEvent) {
        val nether = event.world
        val type = nether.environment
        if (type != World.Environment.NETHER) {
            return
        }
        val world = plugin.server.getWorld("world")
            ?: plugin.server.worlds.first { x -> x.environment == World.Environment.NORMAL }

        val block = event.blocks.firstOrNull() ?: return
        val netherPortalLocation = block.location
        val locationInWorld = netherPortalLocation.toWorldLocation()
        locationInWorld.world = world

        val border = world.worldBorder

        if (border.isInside(locationInWorld)) {
            return
        }

        val entity = event.entity
        if (entity is Player) {
            val text = TextHelper.infoText("You cannot create portal outside Worldborder!", NamedTextColor.RED)
            entity.sendMessage(text)
        }

        event.isCancelled = true
    }
}
