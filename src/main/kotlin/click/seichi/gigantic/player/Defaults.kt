package click.seichi.gigantic.player

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author tar0ss
 */
object Defaults {
    const val MANA = 0
    const val MANA_BAR_NUM = 10
    const val MANA_CHAR = "★"
    const val MANA_LOST_CHAR = "☆"
    val ITEM = ItemStack(Material.AIR)
    const val TOOL_ID = 2
    const val BELT_ID = 1
    const val EFFECT_ID = 0
    // プロフィール更新にかかる時間（秒）
    const val PROFILE_UPDATE_TIME = 1L
    // 寄付履歴表示にかかる時間（秒）
    const val DONATE_HISTORY_LOAD_TIME = 1L
    // Elytra Settings
    const val ELYTRA_BASE_SPEED = 0.05
    const val ELYTRA_BASE_LAUNCH = 3
    // will Settings
    const val WILL_BASIC_UNLOCK_AMOUNT = 1000
    const val WILL_ADVANCED_UNLOCK_AMOUNT = 4000

    // achievement Settings
    // ブロック破壊による実績更新の間隔（ブロック数）
    const val ACHIEVEMENT_BLOCK_BREAK_UPDATE_COUNT = 10
}