package eu.deltacraft.deltacraftteams.listeners

import eu.deltacraft.deltacraftteams.DeltaCraftTeams
import eu.deltacraft.deltacraftteams.managers.ClientManager
import eu.deltacraft.deltacraftteams.types.responses.CheckChatResponse
import eu.deltacraft.deltacraftteams.utils.TextHelper
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack

class AnvilRenameListener(private val plugin: DeltaCraftTeams, private val clientManager: ClientManager) : Listener {

//    @EventHandler
//    fun onAnvilItemRenamed(event: PrepareAnvilEvent) {
//        val res = event.result ?: return
//
//        val player = event.view.player
//
//        val dName = res?.itemMeta?.displayName()
//
//
//    }

    @EventHandler(ignoreCancelled = true)
    fun onItemRenamed(event: InventoryClickEvent) {
        val inventory = event.inventory


        if (inventory !is AnvilInventory) return

        val player = event.whoClicked as Player

        val view = event.view
        val slotIndex = event.rawSlot

        if (slotIndex != view.convertSlot(slotIndex)) return

        /**
         * slot 0 = left item slot
         * slot 1 = right item slot
         * slot 2 = result item slot
         */
        if (slotIndex != 2) return

        val item = event.currentItem ?: return
        val meta = item.itemMeta ?: return
        val dName = meta.displayName() ?: return
        val dNameText = dName as TextComponent

        val displayName = dNameText.content()

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            runBlocking {
                checkItemName(displayName, player, item)
            }
        })


    }

    /**
     * Checks the name of item
     * accepts nonnullable nullable string that can be null or number or even boolean
     */
    private suspend fun checkItemName(itemName: String, player: Player, item: ItemStack) {
        val client = clientManager.getClient()


        val httpRes = client.get<HttpResponse>(path = "api/plugin/check-chat") {
            parameter("message", itemName)
            parameter("uuid", player.uniqueId.toString())
        }

        client.close()

        val status = httpRes.status

        if (status != HttpStatusCode.OK && status != HttpStatusCode.BadRequest) {
            return
        }

        val res = httpRes.receive<CheckChatResponse>()


        if (res.content) return

        val inv = player.inventory

        val metadataLegacy = item.itemMeta?.clone() ?: return

        val metadata = item.itemMeta ?: return

        metadata.displayName(Component.text("Ses hloupej"))
        item.itemMeta = metadata

        val i = inv.contents.firstOrNull { it?.itemMeta?.displayName() == metadataLegacy.displayName() }
        if (i != null) {
            i.itemMeta = metadata
        }

        plugin.logger.info("${player.name} si chtěl pojmenovat item (${item.type.name}) na '$itemName' (${res.message})")

        player.sendMessage(
            TextHelper.infoText("Snažíme se udržovat family-friendly item names.")
                .append(Component.newline())
                .append(Component.text("Vyvaruj se následujícím slovům:"))
                .append(Component.newline())
                .append(Component.text("${res.message}", NamedTextColor.GRAY))
        )

    }
}