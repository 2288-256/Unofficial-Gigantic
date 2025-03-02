package click.seichi.gigantic.sidebar.bars

import click.seichi.gigantic.acheivement.Achievement
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.player.Defaults
import click.seichi.gigantic.player.ExpReason
import click.seichi.gigantic.player.ToggleSetting
import click.seichi.gigantic.sidebar.SideBar
import click.seichi.gigantic.util.NumberUtils.commaSeparated
import click.seichi.gigantic.util.SideBarRow
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.joda.time.DateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * @author tar0ss
 */
object MainBar : SideBar("info") {

    private val infoMap = mutableMapOf<UUID, PlayerInformation>()

    /**
     * [click.seichi.gigantic.player.Defaults.SIDEBAR_RECORD_INTERVAL]秒ごとに呼ばれる
     * @param player 記録するプレイヤー
     */
    fun update(player: Player) {
        infoMap.getOrPut(player.uniqueId) {
            PlayerInformation(player)
        }.run {
            refresh()
            record(player)
        }

        // 表示
        tryShow(player, isForced = false)
    }

    override fun canShow(player: Player): Boolean {
        return true
    }

    override fun getTitle(player: Player): String {
        return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}" +
                "非公式整地鯖(春)"
    }

    override fun getMessageMap(player: Player): Map<SideBarRow, String> {
        val info = infoMap[player.uniqueId] ?: return mapOf()
        val map = mutableMapOf<SideBarRow, String>()

        map[SideBarRow.ONE] = "${ChatColor.GREEN}${ChatColor.BOLD} レベル : " +
                "${ChatColor.WHITE}${info.level}"

        if (ToggleSetting.SCOREBOARD_TOTAL_EXP.getToggle(player)) {
            map[SideBarRow.TWO] = "${ChatColor.GREEN}${ChatColor.BOLD}総経験値: " +
                "${ChatColor.WHITE}${player.wrappedExp.setScale(0, RoundingMode.FLOOR).commaSeparated()}"
        }

        map[SideBarRow.THREE] = "${Defaults.SIDEBAR_HIDE_COLOR}--------------"

        map[SideBarRow.FOUR] = "${ChatColor.GREEN}${ChatColor.BOLD}通常破壊: " +
                if (info.mineBlockPerMinute >= 1000000000.toBigDecimal()) {
                    "${ChatColor.RED}測定不能"
                } else if (info.mineBlockPerMinute >= 1000000.toBigDecimal()) {
                    val formattedValue = (info.mineBlockPerMinute / 1000000.toBigDecimal())
                        .setScale(1, RoundingMode.HALF_UP) // 小数点以下1桁まで
                    "${ChatColor.WHITE}${formattedValue}M/分"
                } else {
                    "${ChatColor.WHITE}${info.mineBlockPerMinute.setScale(0, RoundingMode.HALF_UP)}/分"
                }

        if (Achievement.SPELL_MULTI_BREAK.isGranted(player)) {
            map[SideBarRow.FIVE] = "${ChatColor.DARK_AQUA}${ChatColor.BOLD}魔法破壊: " +
                    if (info.multiBlockPerMinute >= 1000000000.toBigDecimal()) {
                        "${ChatColor.RED}測定不能"
                    } else if (info.multiBlockPerMinute >= 1000000.toBigDecimal()) {
                        val formattedValue = (info.multiBlockPerMinute / 1000000.toBigDecimal())
                            .setScale(1, RoundingMode.HALF_UP) // 小数点以下1桁まで
                        "${ChatColor.WHITE}${formattedValue}M/分"
                    } else {
                        "${ChatColor.WHITE}${info.multiBlockPerMinute.setScale(0, RoundingMode.HALF_UP)}/分"
                    }
        }


        if (Achievement.FIRST_RELIC.isGranted(player)) {
            map[SideBarRow.SIX] = "${ChatColor.GOLD}${ChatColor.BOLD}レリック: " +
                if (info.relicBonusPerMinute >= (99999.99).toBigDecimal()) {
                    when {
                        info.relicBonusPerMinute >= 1000000000.toBigDecimal() -> {
                            "${ChatColor.RED}測定不能"
                        }
                        
                        info.relicBonusPerMinute >= 1000000.toBigDecimal() -> {
                            val formattedValue = (info.relicBonusPerMinute / 1000000.toBigDecimal())
                                .setScale(2, RoundingMode.HALF_UP)
                            "${ChatColor.WHITE}${formattedValue}M/分"
                        }
        
                        info.relicBonusPerMinute >= 1000.toBigDecimal() -> {
                            val formattedValue = (info.relicBonusPerMinute / 1000.toBigDecimal())
                                .setScale(2, RoundingMode.HALF_UP)
                            "${ChatColor.WHITE}${formattedValue}K/分"
                        }
        
                        else -> "${ChatColor.RED}測定不能"
                    }
                } else {
                    "${ChatColor.WHITE}${info.relicBonusPerMinute.setScale(2, RoundingMode.HALF_UP)}/分"
                }
        }        

        map[SideBarRow.SEVEN] = "${Defaults.SIDEBAR_HIDE_COLOR}--- - --- - ---"

        if (Achievement.MANA_STONE.isGranted(player)) {
            map[SideBarRow.EIGHT] = "${ChatColor.AQUA}${ChatColor.BOLD}  マナ  : " +
                    if (info.manaPerMinute >= (99999.99).toBigDecimal()) {
                        when {
                            info.relicBonusPerMinute >= 1000000000.toBigDecimal() -> {
                                "${ChatColor.RED}測定不能"
                            }
                            info.manaPerMinute >= 1000000.toBigDecimal() -> {
                                val formattedValue = (info.manaPerMinute / 1000000.toBigDecimal())
                                    .setScale(2, RoundingMode.HALF_UP)
                                "${ChatColor.WHITE}${formattedValue}M/分"
                            }

                            info.manaPerMinute >= 1000.toBigDecimal() -> {
                                val formattedValue = (info.manaPerMinute / 1000.toBigDecimal())
                                    .setScale(2, RoundingMode.HALF_UP)
                                "${ChatColor.WHITE}${formattedValue}K/分"
                            }

                            else -> "${ChatColor.RED}測定不能"
                        }
                    } else {
                        "${ChatColor.WHITE}${info.manaPerMinute.setScale(2, RoundingMode.HALF_UP)}/分"
                    }
        }
        if (ToggleSetting.SCOREBOARD_MANA.getToggle(player)){
            map[SideBarRow.NINE] = "           ${ChatColor.DARK_AQUA}${player.mana.coerceAtLeast(BigDecimal.ZERO).setScale(1, RoundingMode.HALF_UP)}/${player.maxMana}"
        }

        if (Achievement.MANA_STONE.isGranted(player))
            map[SideBarRow.TEN] = "${Defaults.SIDEBAR_HIDE_COLOR} - - - - - - - - "

        map[SideBarRow.ELEVEN] = "${ChatColor.YELLOW}" +
                "    seichi-haru.pgw.jp  "

        return map
    }

    private class PlayerInformation(player: Player) {

        var level = player.wrappedLevel
            private set

        private val recordQueue: Deque<Record> = LinkedList<Record>()

        val mineBlockPerMinute
            get() = when (recordQueue.size) {
                0 -> BigDecimal.ZERO
                1 -> recordQueue.peekFirst().mineBlock
                else -> recordQueue.peekFirst().mineBlock - recordQueue.peekLast().mineBlock
            }
        val multiBlockPerMinute
            get() = when (recordQueue.size) {
                0 -> BigDecimal.ZERO
                1 -> recordQueue.peekFirst().multiBlock
                else -> recordQueue.peekFirst().multiBlock - recordQueue.peekLast().multiBlock
            }
        val relicBonusPerMinute
            get() = when (recordQueue.size) {
                0 -> BigDecimal.ZERO
                1 -> recordQueue.peekFirst().relicBonus
                else -> recordQueue.peekFirst().relicBonus - recordQueue.peekLast().relicBonus
            }
        val manaPerMinute
            get() = when (recordQueue.size) {
                0 -> BigDecimal.ZERO
                1 -> recordQueue.peekFirst().mana
                else -> recordQueue.peekFirst().mana - recordQueue.peekLast().mana
            }

        // 必要のないデータを削除
        fun refresh() {
            // 1分前よりも前のデータを全て削除
            recordQueue.removeIf {
                it.createdAt.isBefore(DateTime.now().minusMinutes(1).minusSeconds(1))
            }
        }

        // 個人データを収集し保存
        fun record(player: Player) {
            level = player.wrappedLevel

            recordQueue.addFirst(
                Record(
                    player.getWrappedExp(ExpReason.MINE_BLOCK),
                    player.getWrappedExp(ExpReason.SPELL_MULTI_BREAK),
                    player.getWrappedExp(ExpReason.RELIC_BONUS),
                    player.mana
                )
            )
        }

        private class Record(
            val mineBlock: BigDecimal,
            val multiBlock: BigDecimal,
            val relicBonus: BigDecimal,
            val mana: BigDecimal
        ) {
            val createdAt: DateTime = DateTime.now()
        }
    }
}