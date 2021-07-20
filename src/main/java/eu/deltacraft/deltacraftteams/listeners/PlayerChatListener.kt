package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.utils.TranslateTextCodes
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerChatListener(private val plugin: DeltaCraftTeams) : Listener {
    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        val message: TextComponent = TranslateTextCodes.translate((event.message() as TextComponent).content())
        event.message(message)
    }
}