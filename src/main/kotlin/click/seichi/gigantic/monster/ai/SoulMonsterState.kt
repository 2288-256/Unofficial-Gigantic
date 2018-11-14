package click.seichi.gigantic.monster.ai

/**
 * @author tar0ss
 */
enum class SoulMonsterState {
    // 待機
    SEAL,
    // 指示待機
    WAIT,
    // 移動
    MOVE,
    // 攻撃
    ATTACK,
    // 死亡
    DEATH,
    // 消滅
    DISAPPEAR,
    // 召喚者をキル
    KILL_SPAWNER,

    ;
}