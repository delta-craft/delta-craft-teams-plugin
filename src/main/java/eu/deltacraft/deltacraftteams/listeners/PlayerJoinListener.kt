package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.interfaces.IDbConnector
import eu.deltacraft.deltacraftteams.managers.UserConnectionManager
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener(private val plugin: DeltaCraftTeams, dbConnector: IDbConnector) : Listener {
    private val mgr = UserConnectionManager(dbConnector)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        val res = mgr.get(player)
        if (res == null) {
            // TODO: Better component
            player.kick(Component.text("You are not registred"))
            return
        }

        player.sendMessage(res.toString())

    }

}