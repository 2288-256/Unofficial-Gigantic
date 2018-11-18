package click.seichi.gigantic.message.messages

import click.seichi.gigantic.message.ChatMessage
import click.seichi.gigantic.message.ChatMessageProtocol
import click.seichi.gigantic.message.LocalizedText
import click.seichi.gigantic.monster.SoulMonster
import org.bukkit.ChatColor
import java.util.*

/**
 * @author tar0ss
 */
object RelicMessages {

    val DROP_TEXT = { drop: SoulMonster.DropRelic ->
        val relic = drop.relic
        val prob = drop.probability
        LocalizedText(
                Locale.JAPANESE.let { it to "${ChatColor.YELLOW}${relic.getName(it)}を手に入れた!!" }
        )
    }

    val DROP = { drop: SoulMonster.DropRelic ->
        ChatMessage(ChatMessageProtocol.CHAT, DROP_TEXT(drop))
    }

    val SPELL_BOOK_EXPLOSION = LocalizedText(
            Locale.JAPANESE to "魔導書-エクスプロージョン-"
    )

    val GOLDEN_APPLE = LocalizedText(
            Locale.JAPANESE to "黄金の林檎"
    )

    val SPELL_BOOK_AQUA_LINEA = LocalizedText(
            Locale.JAPANESE to "水の魔導書-アクア・リネア-"
    )

    val WILL_CRYSTAL_SAPPHIRE = LocalizedText(
            Locale.JAPANESE to "水の意志結晶-サファイア-"
    )

    val SPELL_BOOK_IGNIS_VOLCANO = LocalizedText(
            Locale.JAPANESE to "火の魔導書-イグニス・ヴォルケーノ-"
    )

    val WILL_CRYSTAL_RUBY = LocalizedText(
            Locale.JAPANESE to "火の意志結晶-ルビー-"
    )

    val SPELL_BOOK_AER_SLASH = LocalizedText(
            Locale.JAPANESE to "空の魔導書-エアル・スラッシュ-"
    )

    val WILL_CRYSTAL_FLUORITE = LocalizedText(
            Locale.JAPANESE to "空の意志結晶-フローライト-"
    )

    val SPELL_BOOK_TERRA_DRAIN = LocalizedText(
            Locale.JAPANESE to "土の魔導書-テラ・ドレイン-"
    )

    val WILL_CRYSTAL_ANDALUSITE = LocalizedText(
            Locale.JAPANESE to "土の意志結晶-アンダルサイト-"
    )

    val SPELL_BOOK_GRAND_NATURA = LocalizedText(
            Locale.JAPANESE to "自然の魔導書-グランド・ナトラ-"
    )

    val WILL_CRYSTAL_JADE = LocalizedText(
            Locale.JAPANESE to "自然の意志結晶-ヒスイ-"
    )

    val PIGS_FEATHER = LocalizedText(
            Locale.JAPANESE to "豚の羽根"
    )

    val BLUE_BLAZE_POWDER = LocalizedText(
            Locale.JAPANESE to "ブルーブレイズのパウダー"
    )

    val CHICKEN_KING_CROWN = LocalizedText(
            Locale.JAPANESE to "鶏キングの王冠"
    )

    val CHICKEN_KING_CROWN_LORE = listOf(
            LocalizedText(
                    Locale.JAPANESE to "10コンボ以上の攻撃時に"
            ),
            LocalizedText(
                    Locale.JAPANESE to "1の追加ダメージ"
            )
    )

    val MANA_STONE = LocalizedText(
            Locale.JAPANESE to "マナ・ストーン"
    )

    val WITHER_SKELETON_SKULL = LocalizedText(
            Locale.JAPANESE to "ウィザースケルトンの頭"
    )


}