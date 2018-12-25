package click.seichi.gigantic.menu.menus

import click.seichi.gigantic.extension.wrappedLocale
import click.seichi.gigantic.item.Button
import click.seichi.gigantic.item.items.menu.NextButton
import click.seichi.gigantic.item.items.menu.PrevButton
import click.seichi.gigantic.item.items.menu.RelicButtons
import click.seichi.gigantic.menu.BookMenu
import click.seichi.gigantic.message.messages.menu.RelicMenuMessages
import click.seichi.gigantic.relic.Relic
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

/**
 * @author tar0ss
 */
object RelicMenu : BookMenu() {

    override val size: Int
        get() = 18

    private const val numOfContentsPerPage = 9

    private val nextButton = NextButton(this)
    private val prevButton = PrevButton(this)

    override fun getMaxPage(player: Player): Int {
        return Relic.getDroppedList(player).size.div(numOfContentsPerPage + 1).plus(1)
    }

    override fun setItem(inventory: Inventory, player: Player, page: Int): Inventory {
        val relicList = Relic.getDroppedList(player)
        val start = (page - 1) * numOfContentsPerPage
        val end = page * numOfContentsPerPage
        (start until end)
                .filter { relicList.getOrNull(it) != null }
                .map { it % numOfContentsPerPage to relicList[it] }
                .toMap()
                .forEach { index, relic ->
                    inventory.setItem(index, RelicButtons.RELIC(relic).findItemStack(player))
                }
        inventory.setItem(numOfContentsPerPage + 3, prevButton.findItemStack(player))
        inventory.setItem(numOfContentsPerPage + 5, nextButton.findItemStack(player))

        return inventory
    }

    override fun getTitle(player: Player, page: Int): String {
        return "${RelicMenuMessages.TITLE.asSafety(player.wrappedLocale)} $page/${getMaxPage(player)}"
    }

    override fun getButton(player: Player, page: Int, slot: Int): Button? {
        val relicList = Relic.getDroppedList(player)
        val index = (page - 1) * numOfContentsPerPage + slot
        val relic = relicList.getOrNull(index) ?: return null
        return when (slot) {
            numOfContentsPerPage + 3 -> prevButton
            numOfContentsPerPage + 5 -> nextButton
            else -> RelicButtons.RELIC(relic)
        }
    }

}