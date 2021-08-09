package eu.deltacraft.deltacraftteams.managers.chest

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane.Priority
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import eu.deltacraft.deltacraftteams.types.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.java.JavaPlugin


class StatsGuiIntegration(private val plugin: JavaPlugin) {
    companion object {
        private val mobDrops = hashMapOf(
            EntityType.GHAST to Material.GHAST_TEAR,
            EntityType.PHANTOM to Material.PHANTOM_MEMBRANE,
            EntityType.RAVAGER to Material.RAVAGER_SPAWN_EGG,
            EntityType.EVOKER to Material.EVOKER_SPAWN_EGG,
            EntityType.VINDICATOR to Material.VINDICATOR_SPAWN_EGG,
            EntityType.PILLAGER to Material.PILLAGER_SPAWN_EGG,
            EntityType.WITCH to Material.WITCH_SPAWN_EGG,
            EntityType.ELDER_GUARDIAN to Material.PRISMARINE_SHARD,
            EntityType.SHULKER to Material.SHULKER_SHELL,
            EntityType.ENDER_DRAGON to Material.DRAGON_HEAD,
            EntityType.WITHER to Material.WITHER_SKELETON_SKULL,
        )
    }

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

        val miningStack = getItemStack(Material.STONE_PICKAXE, "Mining")
        navigationPane.addItem(GuiItem(miningStack) {
            showStats(p, playerName, playerColor, stats.mining, stats)
        })

        val craftingStack = getItemStack(Material.CRAFTING_TABLE, "Crafting")
        navigationPane.addItem(GuiItem(craftingStack) {
            showStats(p, playerName, playerColor, stats.crafting, stats)
        })

        val mobStack = getItemStack(Material.IRON_SWORD, "Mobs")
        navigationPane.addItem(GuiItem(mobStack) {
            showStats(p, playerName, playerColor, stats.mob, stats)
        })

        gui.addPane(navigationPane)

        gui.show(p)
    }

    private fun <T : IStats> showStats(
        p: Player,
        playerName: String,
        playerColor: TextColor,
        stats: ITotalStats<T>,
        original: Stats,
    ) {
        val rows = 6

        val text = Component.text("Bodíky hráče ")
            .append(
                Component.text(playerName, playerColor)
            )
            .append(
                Component.text(" (${stats.totalPoints} bodů)")
            )

        val gui = ChestGui(rows, ComponentHolder.of(text))

        gui.setOnGlobalClick { event: InventoryClickEvent ->
            event.isCancelled = true
        }

        val pane = PaginatedPane(0, 0, 9, rows)

        val chunked = stats.data.chunked(9 * (rows - 1))

        val pages = chunked.size

        for ((i, data) in chunked.withIndex()) {
            // Page
            val itemsPane = OutlinePane(0, 0, 9, rows - 1)
            for (stat in data) {
                var itemStack = ItemStack(Material.AIR)

                if (stat is MaterialStats) {
                    val material = Material.getMaterial(stat.name) ?: continue

                    itemStack = getMaterialItemStackStat(material, stat.count)
                } else if (stat is MobStats) {
                    itemStack = getMobItemStackStat(stat.name, stat.count)
                }

                val guiItem = GuiItem(itemStack)
                itemsPane.addItem(guiItem)
            }

            pane.addPane(i, itemsPane)
        }

        gui.addPane(pane)

        //page selection
        val back = StaticPane(0, rows - 1, 1, 1)
        val forward = StaticPane(8, rows - 1, 1, 1)
        val menu = StaticPane(4, rows - 1, 2, 1)

        back.addItem(GuiItem(getItemStack(Material.ARROW, "Previous page")) {
            pane.page = pane.page - 1
            if (pane.page == 0) {
                back.isVisible = false
            }
            forward.isVisible = true
            gui.update()
        }, 0, 0)

        back.isVisible = false

        forward.addItem(GuiItem(getItemStack(Material.ARROW, "Next page")) {
            pane.page = pane.page + 1
            if (pane.page == pane.pages - 1) {
                forward.isVisible = false
            }
            back.isVisible = true
            gui.update()
        }, 0, 0)

        if (pages < 2) {
            forward.isVisible = false
        }

        val item = GuiItem(getItemStack(Material.CHEST, "Menu")) {
            showGui(p, playerName, playerColor, original)
        }

        menu.addItem(item, 0, 0)
        menu.addItem(item, 1, 0)

        gui.addPane(back)
        gui.addPane(forward)
        gui.addPane(menu)

        gui.show(p)
    }

    private fun getItemStack(material: Material, name: String? = null): ItemStack {
        val stack = ItemStack(material)

        val meta = stack.itemMeta
        if (name != null) {
            meta.displayName(Component.text(name))
        }

        hideAllAttributes(meta)

        stack.itemMeta = meta

        return stack
    }

    private fun getMaterialItemStackStat(material: Material, count: Int): ItemStack {
        val stack = ItemStack(material)

        val meta = stack.itemMeta

        val lore = meta.lore() ?: mutableListOf()
        lore.add(Component.text("${count}x"))
        meta.lore(lore)

        hideAllAttributes(meta)

        stack.itemMeta = meta

        return stack
    }

    private fun getMobItemStackStat(name: String, count: Int): ItemStack {
        val entity = EntityType.values().firstOrNull { it.name.equals(name, true) }

        val material = getMaterialFromEntity(entity)

        val stack = ItemStack(material)

        val meta = stack.itemMeta

        meta.displayName(Component.text(name))

        val lore = meta.lore() ?: mutableListOf()
        lore.add(Component.text("${count}x"))
        meta.lore(lore)

        hideAllAttributes(meta)

        stack.itemMeta = meta

        return stack
    }

    private fun getMaterialFromEntity(entity: EntityType?): Material {
        if (entity == null) {
            return Material.PAPER
        }

        return mobDrops[entity] ?: Material.PAPER
    }

    private fun hideAllAttributes(meta: ItemMeta) {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_DESTROYS,
            ItemFlag.HIDE_PLACED_ON,
            ItemFlag.HIDE_POTION_EFFECTS,
            ItemFlag.HIDE_DYE)
    }
}