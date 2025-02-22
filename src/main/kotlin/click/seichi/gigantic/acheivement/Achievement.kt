package click.seichi.gigantic.acheivement

import click.seichi.gigantic.Gigantic
import click.seichi.gigantic.belt.Belt
import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.config.DebugConfig
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.message.DiscordWebhookNotifier
import click.seichi.gigantic.message.LinedChatMessage
import click.seichi.gigantic.message.Message
import click.seichi.gigantic.message.messages.AchievementMessages
import click.seichi.gigantic.mission.Mission
import click.seichi.gigantic.relic.Relic
import click.seichi.gigantic.sound.DetailedSound
import click.seichi.gigantic.sound.sounds.PlayerSounds
import click.seichi.gigantic.tool.Tool
import click.seichi.gigantic.will.Will
import click.seichi.gigantic.will.WillGrade
import org.bukkit.entity.Player

/**
 * @author tar0ss
 *
 *
 */
enum class Achievement(
        val id: Int,
        private val canGranting: (Player) -> Boolean,
        // アンロック時に処理される
        val action: (Player) -> Unit = {},
        val grantMessage: Message? = null,
        val broadcastMessage: (Player) -> Message? = { null },
        val broadcastSound: DetailedSound? = null,
        private val priority: UpdatePriority = UpdatePriority.NORMAL
) {
    // messages
    JOIN_SERVER(0, { true }, action = {
        Tool.PICKEL.grant(it)
        Tool.SHOVEL.grant(it)
        Tool.AXE.grant(it)
        Belt.DEFAULT.grant(it)
    }, grantMessage = AchievementMessages.FIRST_JOIN
            , broadcastMessage = { AchievementMessages.FIRST_JOIN_ALL(it) }),
    FIRST_LEVEL_UP(1, {
        it.wrappedLevel >= 2
    }, grantMessage = AchievementMessages.FIRST_LEVEL_UP),
    FIRST_WILL(2, {
        Will.values().firstOrNull { will -> it.hasAptitude(will) } != null
    }, grantMessage = AchievementMessages.FIRST_WILL
            , priority = UpdatePriority.LOWEST),
    FIRST_RELIC(3, {
        Relic.values().firstOrNull { relic -> it.hasRelic(relic) } != null
    }, grantMessage = AchievementMessages.FIRST_RELIC),
    FIRST_ADVANCED_WILL(4, {
        Will.values()
                .filter { it.grade == WillGrade.ADVANCED }
                .firstOrNull { will -> it.hasAptitude(will) } != null
    }, grantMessage = AchievementMessages.FIRST_ADVANCED_WILL,
            priority = UpdatePriority.LOWEST),
    TUTORIAL(5, {
        it.wrappedLevel >= 200
    }, action = {DiscordWebhookNotifier.sendLevelNotification(it.name,200)}
        , broadcastMessage = { AchievementMessages.LEVEL_UP_ALL(it,200) }
        , broadcastSound = PlayerSounds.ACHIEVE_TUTORIAL),
    FIRST_PRE_SENSE(6, {
        Will.values().firstOrNull { will -> it.isProcessed(will) } != null
    }, grantMessage = AchievementMessages.FIRST_PRE_SENSE),

    // systems
    MANA_STONE(100,
            {
                it.wrappedLevel >= 10
            }, grantMessage = AchievementMessages.MANA_STONE,
            priority = UpdatePriority.HIGHEST),
    TELEPORT_PLAYER(101,
            {
                it.wrappedLevel >= 4
            }, grantMessage = AchievementMessages.TELEPORT_PLAYER),
    QUEST(102,
            {
                false /*it.wrappedLevel >= 5*/
            }, priority = UpdatePriority.HIGHEST),
    TELEPORT_LAST_DEATH(103,
            {
                it.wrappedLevel >= 7
            }, grantMessage = AchievementMessages.TELEPORT_LAST_DEATH),
    /*   スキル移転により使用せず
        JUMP(104, {
            it.wrappedLevel >= 15
        }, grantMessage = AchievementMessages.JUMP),*/
    TELEPORT_HOME(105,
            {
                it.wrappedLevel >= 6
            }, grantMessage = AchievementMessages.TELEPORT_HOME),
    // 使用するまで保留
    /*SWORD(XXX,{
        it.wrappedLevel >= 17
    },action = {
        Tool.SWORD.grant(it)
        it.offer(Keys.TOOL_TOGGLE_MAP[Tool.SWORD]!!,false)
    }, grantMessage = AchievementMessages.SWORD),*/
    DAILY_MISSION_UNLOCK(106,
        {
            it.wrappedLevel >= WillGrade.ADVANCED.unlockLevel
        },
        action= { Mission.missionCreate(it, 1) },
        grantMessage = AchievementMessages.UNLOCK_DAILY_MISSION
    ),

    // skills
    SKILL_FLASH(200,
            {
                it.wrappedLevel >= 5/*Quest.PIG.isCleared(it)*/
            }, grantMessage = AchievementMessages.UNLOCK_FLASH),
    SKILL_MINE_BURST(201,
            {
                it.wrappedLevel >= 8/*Quest.BLAZE.isCleared(it)*/
            }, grantMessage = AchievementMessages.UNLOCK_SKILL_MINE_BURST),
    SKILL_MINE_COMBO(202,
            {
                it.wrappedLevel >= 3
            }, grantMessage = AchievementMessages.UNLOCK_SKILL_MINE_COMBO),
    SKILL_JUMP(203,
            {
                it.wrappedLevel >= 18
            }, grantMessage = AchievementMessages.JUMP),
    SKILL_FOCUS_TOTEM(204,
            {
                it.wrappedLevel >= 15
            }, action =
    {
        it.offer(Keys.TOTEM, 1)
    }, grantMessage = AchievementMessages.FOCUT_TOTEM),
    // spells
    SPELL_STELLA_CLAIR(300,
            {
                MANA_STONE.isGranted(it)
            }, grantMessage = AchievementMessages.UNLOCK_SPELL_STELLA_CLAIR),
    SPELL_MULTI_BREAK(301,
            {
                MANA_STONE.isGranted(it)
            }, grantMessage = AchievementMessages.UNLOCK_SPELL_MULTI_BREAK),
    SPELL_SKY_WALK(302,
            {
                MANA_STONE.isGranted(it) && it.wrappedLevel >= 18
            }, grantMessage = AchievementMessages.UNLOCK_SPELL_SKY_WALK),
    SPELL_LUNA_FLEX(303,
            {
                MANA_STONE.isGranted(it) && it.wrappedLevel >= 28
            }, grantMessage = AchievementMessages.UNLOCK_SPELL_LUNA_FLEX),

    // quest order

    // MAIN QUEST
    /*QUEST_BEGINS_ORDER(401, {
        QUEST.isGranted(it)
    }, action = {
        Quest.BEGIN.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER_FIRST),
    QUEST_PIG_ORDER(402, {
        Quest.BEGIN.isCleared(it) &&
                it.wrappedLevel >= 7
    }, action = {
        Quest.PIG.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_BLAZE_ORDER(403, {
        Quest.PIG.isCleared(it) &&
                it.wrappedLevel >= 9
    }, action = {
        Quest.BLAZE.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),*/
    /*QUEST_LADON_ORDER(400, {
        false
    }, action = {
        Quest.LADON.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_UNDINE_ORDER(401, {
        MANA_STONE.isGranted(it) &&
                Will.AQUA.isAptitude(it)
    }, action = {
        Quest.UNDINE.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_SALAMANDRA_ORDER(402, {
        MANA_STONE.isGranted(it) &&
                Will.IGNIS.isAptitude(it)
    }, action = {
        Quest.SALAMANDRA.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_SYLPHID_ORDER(403, {
        MANA_STONE.isGranted(it) &&
                Will.AER.isAptitude(it)
    }, action = {
        Quest.SYLPHID.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_NOMOS_ORDER(404, {
        MANA_STONE.isGranted(it) &&
                Will.TERRA.isAptitude(it)
    }, action = {
        Quest.NOMOS.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_LOA_ORDER(405, {
        MANA_STONE.isGranted(it) &&
                Will.NATURA.isAptitude(it)
    }, action = {
        Quest.LOA.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_PIG_ORDER(406, {
        Quest.BEGIN.isCleared(it) &&
                it.wrappedLevel >= 7
    }, action = {
        Quest.PIG.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_BLAZE_ORDER(407, {
        Quest.PIG.isCleared(it) &&
                it.wrappedLevel >= 9
    }, action = {
        Quest.BLAZE.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_CHICKEN_ORDER(408, {
        false
    }, action = {
        Quest.CHICKEN.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_WITHER_ORDER(409, {
        false
    }, action = {
        Quest.WITHER.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_BEGINS_ORDER(410, {
        QUEST.isGranted(it)
    }, action = {
        Quest.BEGIN.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER_FIRST),
    QUEST_TURTLE_ORDER(411, {
        false
    }, action = {
        Quest.TURTLE.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_SPIDER_ORDER(412, {
        false
    }, action = {
        Quest.SPIDER.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_ZOMBIE_ORDER(413, {
        false
    }, action = {
        Quest.ZOMBIE.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_SKELETON_ORDER(414, {
        false
    }, action = {
        Quest.SKELETON.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_ORC_ORDER(415, {
        false
    }, action = {
        Quest.ORC.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_GHOST_ORDER(416, {
        false
    }, action = {
        Quest.GHOST.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_PARROT_ORDER(417, {
        false
    }, action = {
        Quest.PARROT.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_SLIME_ORDER(418, {
        false
    }, action = {
        Quest.SLIME.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),
    QUEST_ENDER_MAN_ORDER(419, {
        false
    }, action = {
        Quest.ENDER_MAN.order(it)
    }, grantMessage = AchievementMessages.QUEST_ORDER),*/

    // will
    WILL_BASIC(500,
            {
                it.wrappedLevel >= WillGrade.BASIC.unlockLevel
            }, priority = UpdatePriority.HIGH),

    WILL_AQUA(501,
            {
                WILL_BASIC.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.AQUA]!!) >= Will.AQUA.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.AQUA]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.AQUA)),
    WILL_IGNIS(502,
            {
                WILL_BASIC.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.IGNIS]!!) >= Will.IGNIS.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.IGNIS]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.IGNIS)),
    WILL_AER(503,
            {
                WILL_BASIC.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.AER]!!) >= Will.AER.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.AER]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.AER)),
    WILL_TERRA(504,
            {
                WILL_BASIC.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.TERRA]!!) >= Will.TERRA.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.TERRA]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.TERRA)),
    WILL_NATURA(505,
            {
                WILL_BASIC.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.NATURA]!!) >= Will.NATURA.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.NATURA]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.NATURA)),


    WILL_ADVANCED(550,
            {
                it.wrappedLevel >= WillGrade.ADVANCED.unlockLevel
            }, priority = UpdatePriority.HIGH),
    WILL_GLACIES(551,
            {
                WILL_ADVANCED.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.GLACIES]!!) >= Will.GLACIES.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.GLACIES]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.GLACIES)),
    WILL_LUX(552,
            {
                WILL_ADVANCED.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.LUX]!!) >= Will.LUX.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.LUX]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.LUX)),
    WILL_SOLUM(553,
            {
                WILL_ADVANCED.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.SOLUM]!!) >= Will.SOLUM.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.SOLUM]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.SOLUM)),
    WILL_UMBRA(554,
            {
                WILL_ADVANCED.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.UMBRA]!!) >= Will.UMBRA.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.UMBRA]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.UMBRA)),
    WILL_VENTUS(555,
            {
                WILL_ADVANCED.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.VENTUS]!!) >= Will.VENTUS.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.VENTUS]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.VENTUS)),


    WILL_SPECIAL(600,
            {
                it.wrappedLevel >= WillGrade.SPECIAL.unlockLevel
            }, priority = UpdatePriority.HIGH),
    WILL_SAKURA(601,
            {
                WILL_SPECIAL.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.SAKURA]!!) >= Will.SAKURA.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.SAKURA]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.SAKURA)),
    WILL_MIO(602,
            {
                WILL_SPECIAL.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.MIO]!!) >= Will.MIO.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.MIO]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.MIO)),
    WILL_KAEDE(603,
            {
                WILL_SPECIAL.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.KAEDE]!!) >= Will.KAEDE.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.KAEDE]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.KAEDE)),
    WILL_REI(604,
            {
                WILL_SPECIAL.isGranted(it) &&
                        it.getOrPut(Keys.WILL_SECRET_MAP[Will.REI]!!) >= Will.REI.grade.unlockAmount
            }, action =
    {
        it.offer(Keys.APTITUDE_MAP[Will.REI]!!, true)
    }, grantMessage = AchievementMessages.WILL(Will.REI)),

    // 剣の不具合対応
    UPDATE_USE_SWORD(900, { true }, action =
        {
            Tool.SWORD.grant(it)
        }, grantMessage = AchievementMessages.UPDATE_USE_SWORD),
    ;

    /**1から順に [update] される**/
    enum class UpdatePriority(val amount: Int) {
        HIGHEST(1), HIGH(2), NORMAL(3), LOW(4), LOWEST(5)
    }

    companion object {

        // 重複確認
        val hasDuplicateId = values().size != values().map { it.id }.toSet().size

        // 強制的にプレイヤー表示部分を更新したい場合は[isForced]をtrueに設定
        fun update(player: Player, isForced: Boolean = false) {
            var isGranted = false
            var delay = 1L
            values().sortedBy { it.priority.amount }
                    .filter { it.canGrant(player) && !it.isGranted(player) }
                    .apply {
                        if (isNotEmpty()) isGranted = true
                    }.forEach {
                        // 解除処理
                        player.offer(Keys.ACHIEVEMENT_MAP.getValue(it), true)
                        // 初期処理実行
                        it.action(player)

                        // メッセージ送信
                        if (!Gigantic.IS_DEBUG || DebugConfig.ACHIEVEMENT_ANNOUNCE) {
                            runTaskLater(delay) {
                                if (!player.isValid) return@runTaskLater
                                it.broadcastMessage(player)?.broadcast()
                                it.broadcastSound?.broadcast()
                                it.grantMessage?.sendTo(player)
                            }
                            if (it.grantMessage is LinedChatMessage) {
                                // メッセージ終了まで待機 + メッセージ間隔用45L
                                delay += it.grantMessage.duration.plus(45L)
                            }
                        }
                    }
            if (!isGranted && !isForced) return
            player.updateDisplay(true, true)

        }
    }

    private fun canGrant(player: Player) =
            if (DebugConfig.ACHIEVEMENT_UNLOCK && Gigantic.IS_DEBUG) true
            else canGranting(player)

    fun isGranted(player: Player) =
            canGrant(player) && Keys.ACHIEVEMENT_MAP[this]?.let { player.getOrPut(it) } ?: false


}