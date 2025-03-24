package click.seichi.gigantic.item.items.menu

import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.item.Button
import click.seichi.gigantic.menu.menus.MonsterBookMenu
import click.seichi.gigantic.message.messages.menu.MonsterBookMenuMessages
import click.seichi.gigantic.monster.SoulMonster
import click.seichi.gigantic.sound.sounds.MenuSounds
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

object MonsterBookButtons {
    val DIFFICULTY_BUTTON: (SoulMonster.DifficultyType) -> Button = { difficultyType: SoulMonster.DifficultyType ->
        object : Button {
            override fun toShownItemStack(player: Player): ItemStack {
                val locale = player.wrappedLocale
                return itemStackOf(difficultyType.material) {
                    setDisplayName(difficultyType.displayName.asSafety(locale))
                    clearLore()
                    val allMonster = SoulMonster.values().filter { it.difficultyType == difficultyType }.size
                    addLore("${ChatColor.GREEN}" + MonsterBookMenuMessages.COMPLETEION_RATE.asSafety(locale) + "${ChatColor.WHITE}?/$allMonster")
                }
            }

            override fun tryClick(player: Player, event: InventoryClickEvent): Boolean {
                val currentCategory = player.getOrPut(Keys.MENU_MONSTERBOOK_CATEGORY)
                if (currentCategory == difficultyType) return false
                player.offer(Keys.MENU_MONSTERBOOK_CATEGORY, difficultyType)
                MonsterBookMenu.open(player, isFirst = false, playSound = false)
                MenuSounds.CATEGORY_CHANGE.playOnly(player)
                return true
            }
        }
    }
    val MONSTER_INFO: (SoulMonster) -> Button = { monster: SoulMonster ->
        object : Button {
            override fun toShownItemStack(player: Player): ItemStack {
                val client = monster.getBookClient(player)
                val locale = player.wrappedLocale
                if (client != null) {
                    return monster.getIcon().apply {
                        val defeatRate = if (client.encounterCount > 0) {
                            val rate = client.defeatCount.toDouble() / client.encounterCount.toDouble() * 100.0
                            val formatter = java.text.DecimalFormat("0.##")
                            "${formatter.format(rate)}%"
                        } else {
                            "0%"
                        }
                        setDisplayName(monster.getName(locale))
                        clearLore()
                        if (client.isEligible){
                            addLore("${ChatColor.GREEN}${ChatColor.BOLD}" + MonsterBookMenuMessages.AFFINITY.asSafety(locale))
                        }else{
                            addLore("${ChatColor.RED}${ChatColor.BOLD}" + MonsterBookMenuMessages.NO_ADDINITY.asSafety(locale))
                        }
                        addLore("${ChatColor.WHITE}${ChatColor.BOLD}" + MonsterBookMenuMessages.DEFEAT_STATUS.asSafety(locale))
                        addLore("${ChatColor.GRAY}" + MonsterBookMenuMessages.ENCOUNT_COUNT.asSafety(locale) + "${ChatColor.WHITE}${client.encounterCount}")
                        addLore("${ChatColor.GRAY}" + MonsterBookMenuMessages.DEFEAT_COUNT.asSafety(locale) + "${ChatColor.WHITE}${client.defeatCount}")
                        addLore("${ChatColor.GRAY}" + MonsterBookMenuMessages.DEFEAT_RATE.asSafety(locale) + "${ChatColor.WHITE}$defeatRate")
                        addLore("${ChatColor.GRAY}" + MonsterBookMenuMessages.FIRST_ENCOUNT_DATE.asSafety(locale) + client.firstEncounterDate.toString("yyyy/MM/dd kk:mm:ss"))
                        addLore("${ChatColor.DARK_GRAY}ID:${monster.id}")
                    }
                }else{
                    return itemStackOf(Material.FIREWORK_STAR) {
                        setDisplayName("???")
                        clearLore()
                        addLore("${ChatColor.GRAY}" + MonsterBookMenuMessages.NOT_ENCOUNTERED.asSafety(locale))
                        addLore("${ChatColor.DARK_GRAY}ID:${monster.id}")
                    }
                }
            }
        }
    }
}