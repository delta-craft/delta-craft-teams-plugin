package eu.deltacraft.deltacraftteams.managers.chest

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import eu.deltacraft.deltacraftteams.types.Stats
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


class StatsGuiIntegration(private val plugin: JavaPlugin) {
    fun showGui(p: Player, playerName: String, playerColor: TextColor, stats: Stats) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            showMainPage(p, playerName, playerColor, stats)
        })
    }

    private fun showMainPage(p: Player, playerName: String, playerColor: TextColor, stats: Stats) {
        val text = Component.text("Bodíky hráče ")
            .append(
                Component.text(playerName, playerColor)
            )

        val gui = ChestGui(3, ComponentHolder.of(text))

        gui.setOnGlobalClick { event: InventoryClickEvent ->
            event.isCancelled = true
        }

        val background = OutlinePane(0, 0, 9, 3, Priority.LOWEST)
        background.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
        background.setRepeat(true)

        gui.addPane(background)

        val navigationPane = OutlinePane(3, 1, 3, 1)

        val miningStack = ItemStack(Material.STONE_PICKAXE)
        val miningMeta = miningStack.itemMeta
        miningMeta.displayName(Component.text("Mining"))
        miningStack.itemMeta = miningMeta

        navigationPane.addItem(GuiItem(miningStack))

        val craftingStack = ItemStack(Material.CRAFTING_TABLE)
        val craftingMeta = craftingStack.itemMeta
        craftingMeta.displayName(Component.text("Crafting"))
        craftingStack.itemMeta = craftingMeta

        navigationPane.addItem(GuiItem(craftingStack))

        val mobStack = ItemStack(Material.IRON_SWORD)
        val mobMeta = mobStack.itemMeta
        mobMeta.displayName(Component.text("Mobs"))
        mobStack.itemMeta = mobMeta

        navigationPane.addItem(GuiItem(mobStack) { e: InventoryClickEvent? ->
            run {

                gui.update()
            }
        })

        gui.addPane(navigationPane)

        gui.show(p)
    }

    private fun showStats(p: Player, playerName: String, playerColor: TextColor, stats: Stats) {
        val rows = 6

        val text = Component.text("Bodíky hráče ")
            .append(
                Component.text(playerName, playerColor)
            )

        val gui = ChestGui(rows, ComponentHolder.of(text))

        gui.setOnGlobalClick { event: InventoryClickEvent ->
            event.isCancelled = true
        }

        val pane = PaginatedPane(0, 0, 9, rows)

        gui.addPane(pane)

        //page selection
        val back = StaticPane(2, 5, 1, 1)
        val forward = StaticPane(6, 5, 1, 1)

        back.addItem(GuiItem(ItemStack(Material.ARROW)) {
            pane.page = pane.page - 1
            if (pane.page == 0) {
                back.isVisible = false
            }
            forward.isVisible = true
            gui.update()
        }, 0, 0)

        back.isVisible = false

        forward.addItem(GuiItem(ItemStack(Material.ARROW)) {
            pane.page = pane.page + 1
            if (pane.page == pane.pages - 1) {
                forward.isVisible = false
            }
            back.isVisible = true
            gui.update()
        }, 0, 0)

        gui.addPane(back)
        gui.addPane(forward)

        gui.show(p)
    }
}