package click.seichi.gigantic.menu.menus

import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.item.Button
import click.seichi.gigantic.item.items.menu.MonsterBookButtons
import click.seichi.gigantic.item.items.menu.NextButton
import click.seichi.gigantic.item.items.menu.PrevButton
import click.seichi.gigantic.menu.BookMenu
import click.seichi.gigantic.message.messages.menu.MonsterBookMenuMessages
import click.seichi.gigantic.monster.SoulMonster
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object MonsterBookMenu : BookMenu() {
    override val size: Int
        get() = 6 * 9

    private const val numOfContentsPerPage = 3 * 9

    private const val offset = 2 * 9

    init {
         registerButton(1, MonsterBookButtons.DIFFICULTY_BUTTON(SoulMonster.DifficultyType.Easy))
         registerButton(3, MonsterBookButtons.DIFFICULTY_BUTTON(SoulMonster.DifficultyType.Normal))
         registerButton(5, MonsterBookButtons.DIFFICULTY_BUTTON(SoulMonster.DifficultyType.Hard))
         registerButton(7, MonsterBookButtons.DIFFICULTY_BUTTON(SoulMonster.DifficultyType.All))
         registerButton(size - 6, PrevButton(this))
         registerButton(size - 4, NextButton(this))
    }

    override fun getMaxPage(player: Player): Int {
        return player.getOrPut(Keys.MENU_MONSTERBOOK_LIST).size.minus(1).div(numOfContentsPerPage).plus(1).coerceAtLeast(1)
    }

    override fun onOpen(player: Player, page: Int, isFirst: Boolean) {
        if (isFirst) {
            player.offer(Keys.MENU_MONSTERBOOK_CATEGORY, SoulMonster.DifficultyType.All)
        }
        val category = player.getOrPut(Keys.MENU_MONSTERBOOK_CATEGORY)
            player.offer(Keys.MENU_MONSTERBOOK_LIST,
            SoulMonster.values()
                .filter { category.isContain(player, it) }
                .toList()
        )
    }

    override fun getTitle(player: Player, page: Int): String {
        return MonsterBookMenuMessages.TITLE.asSafety(player.wrappedLocale) + " $page/${getMaxPage(player)}"
    }

    override fun setItem(inventory: Inventory, player: Player, page: Int): Inventory {
        val contentList = player.getOrPut(Keys.MENU_MONSTERBOOK_LIST)
        val start = (page - 1) * numOfContentsPerPage
        val end = page * numOfContentsPerPage
        (start until end)
                .filter { contentList.getOrNull(it) != null }
                .map { it % numOfContentsPerPage to contentList[it] }
                .toMap()
                .forEach { index, monstar ->
                    inventory.setItemAsync(player, index + offset, MonsterBookButtons.MONSTER_INFO(monstar))
                }
        getButtonMap().forEach { slot, button ->
            inventory.setItemAsync(player, slot, button)
        }
        return inventory
    }

    override fun getButton(player: Player, page: Int, slot: Int): Button? {
        val index = (page - 1) * numOfContentsPerPage + slot - offset
        return getButtonMap()[slot] ?: MonsterBookButtons.MONSTER_INFO(player.getOrPut(Keys.MENU_MONSTERBOOK_LIST)[index])
    }

}