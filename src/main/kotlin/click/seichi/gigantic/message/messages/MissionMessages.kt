package click.seichi.gigantic.message.messages

import click.seichi.gigantic.message.ChatMessageProtocol
import click.seichi.gigantic.message.LinedChatMessage
import click.seichi.gigantic.message.LocalizedText
import click.seichi.gigantic.relic.Relic
import click.seichi.gigantic.will.Will
import org.bukkit.ChatColor
import java.util.*

/**
 * @author 2288-256
 */
object MissionMessages {

    val PREFIX = LocalizedText(
            Locale.JAPANESE to "${ChatColor.YELLOW}[ミッション]"
    )

    val MISSION_CREATE = LinedChatMessage(ChatMessageProtocol.CHAT,
        LocalizedText(
            Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.WHITE}ミッションが更新されました"
        )
    )

    val MISSION_COMPLETE = { missionName: String ->
        LocalizedText(
            Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.WHITE}ミッション「$missionName」を達成しました！"
        )
    }

    val MISSION_REWARD_GET_ETHEL_ONE = { will: Will, amount: Int ->
        LocalizedText(
            Locale.JAPANESE.let {
                it to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.WHITE}報酬として「${will.chatColor}${will.getName(it)}${ChatColor.WHITE}」のエーテルを${amount}個受け取りました"
            }
        )
    }

    val MISSION_REWARD_GET_RELIC_ONE = { relic: Relic, ->
        LocalizedText(
            Locale.JAPANESE.let {
                it to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.WHITE}報酬として「${Will.findByRelic(relic)?.chatColor}${relic.getName(it)}${ChatColor.WHITE}」を受け取りました"
            }
        )
    }

    val MISSION_REWARD_GET_RELIC_MULTIPLE = { amount: Int ->
        LocalizedText(
            Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.WHITE}報酬としてレリックを${amount}個受け取りました"
        )
    }

    val MISSION_REWARD_GET_RELIC_MORE_START = LocalizedText(
        Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.WHITE}========= 受け取ったレリック =========="
    )

    val MISSION_REWARD_GET_RELIC_MORE_END = LocalizedText(
        Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.WHITE}============================="
    )
    val MISSION_REWARD_GET_RELIC_DETAIL = { relic: Relic, amount: Int ->
        LocalizedText(
            Locale.JAPANESE.let {
                it to "${PREFIX.asSafety(Locale.JAPANESE)} ${Will.findByRelic(relic)?.chatColor}${relic.getName(it)} ${ChatColor.GRAY}x$amount"
            }
        )
    }
    val NO_MISSION_ERROR = LocalizedText(
        Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.RED}ミッションが見つかりませんでした"
    )

    val REWARD_ALREADY_RECEIVED_ERROR = LocalizedText(
        Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.RED}報酬はすでに受け取り済みです"
    )

    val NO_REWARD_ERROR = LocalizedText(
        Locale.JAPANESE to "${PREFIX.asSafety(Locale.JAPANESE)} ${ChatColor.RED}受け取っていない報酬が見つかりませんでした"
    )

}