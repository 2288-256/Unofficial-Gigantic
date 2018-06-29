package click.seichi.gigantic.skill

import click.seichi.gigantic.message.LocalizedString
import click.seichi.gigantic.message.lang.skill.SkillLang

/**
 * @author tar0ss
 */
enum class SkillState(
        val canFire: Boolean,
        val localizedName: LocalizedString = if (canFire) SkillLang.ACTIVATE else SkillLang.NOT_ACTIVATE
) {
    NOT_LOADING(false),
    ACTIVATE(true),
//    NOT_ACTIVATE(false, "OFF"),
//    LOCK(false, "%DELETE%"),
//    NOT_SURVIVAL(false, "NOT_SURVIVAL"),
//    NOT_SEICHI_WORLD(false, "使用不可能ワールド"),
//    FLYING(false, "フライ使用中"),
//    NOT_SEICHI_TOOL(false, "使用不可能ツール"),
//    SAFETY_ACTUATED(false, "安全装置作動"),
//    WORLD_GUARD(false,"自保護外"),
//    SKILLED(false,"スキル発動済み"),
//    UNDER_PLAYER(false,"破壊位置が低い"),
//    NO_DURABILITY(false,"耐久不足"),
//    NO_MANA(false,"マナ不足"),
//    COOL_TIME(false,"クールタイム中"),
//    LIQUID(false,"液体"),
//    SOLID(false,"固体"),
//    NO_TARGET(false,"ターゲット無し"),
//    NO_FAIRY(false,"妖精要請中"),
//    NO_BLOCK(false,"ON"),
//    UPPER_BLOCK(false,"上方ブロック検知")
}