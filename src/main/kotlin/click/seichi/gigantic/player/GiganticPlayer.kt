package click.seichi.gigantic.player

import click.seichi.gigantic.player.belt.Belt
import click.seichi.gigantic.player.components.*
import click.seichi.gigantic.player.defalutInventory.DefaultInventory
import org.bukkit.entity.Player
import java.util.*

interface GiganticPlayer {
    val player: Player
    val uniqueId: UUID
    var locale: Locale
    val mana: Mana
    val mineBlock: MineBlock
    val level: Level
    val abilityPoint: AbilityPoint
    val memory: Memory
    val aptitude: WillAptitude
    // TODO implements
    val explosionLevel: Int
    val defaultInventory: DefaultInventory
    var belt: Belt
    val breakCombo: BreakCombo

    fun switchBelt()
}
