package click.seichi.gigantic.battle

import click.seichi.gigantic.Gigantic
import click.seichi.gigantic.animation.animations.BattleMonsterAnimations
import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.message.messages.BattleMessages
import click.seichi.gigantic.monster.MonsterBookClient
import click.seichi.gigantic.monster.SoulMonster
import click.seichi.gigantic.monster.ai.AttackBlock
import click.seichi.gigantic.monster.ai.SoulMonsterState
import click.seichi.gigantic.sound.sounds.SoulMonsterSounds
import click.seichi.gigantic.topbar.bars.BattleBars
import click.seichi.gigantic.util.Random.weightedRandom
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.boss.BossBar
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.EulerAngle
import org.joda.time.DateTime

/**
 * @author tar0ss
 */
class BattleMonster(
        private val monster: SoulMonster,
        spawner: BattlePlayer,
        private val chunk: Chunk
) {
    private val locale = spawner.player.wrappedLocale
    // モンスターAI
    private val ai = monster.createAIInstance()
    // 攻撃ブロック
    private val attackBlocks: MutableSet<AttackBlock> = mutableSetOf()
    // モンスターのボスバー
    private val bossBar: BossBar = Bukkit.getServer().createInvisibleBossBar()
    // モンスターの実体
    private lateinit var entity: ArmorStand
    private lateinit var location: Location
    private val eyeLocation: Location
        get() = entity.eyeLocation
    // 攻撃対象プレイヤー
    var attackTarget: BattlePlayer = spawner
        private set
    // 状態遷移用
    var state: SoulMonsterState = SoulMonsterState.SEAL
        private set(value) {
            field = when (field) {
                SoulMonsterState.DISAPPEAR,
                SoulMonsterState.DEATH -> field
                else -> value
            }
        }
    // 目的地
    var destination: Location? = null
        private set

    var health = monster.parameter.health

    var lastAttackTicks: Long = 0L

    private var disappearCount = 0

    private val totalDamageMap = mutableMapOf<Player, Long>()

    private val attackBlockData = Bukkit.createBlockData(monster.parameter.attackMaterial)

    private val healBlockData = Bukkit.createBlockData(Material.PINK_GLAZED_TERRACOTTA)

    private val debuffBlockData = Bukkit.createBlockData(Material.PURPLE_GLAZED_TERRACOTTA)

    private val debuffList = listOf(
        Pair(PotionEffectType.SLOW,2),
        Pair(PotionEffectType.SLOW_DIGGING,1),
        Pair(PotionEffectType.BLINDNESS,3)
    )

    fun leave(battlePlayer: BattlePlayer) {
        bossBar.removePlayer(battlePlayer.player)
    }

    fun remove() {
        bossBar.removeAll()
        entity.remove()
        attackBlocks.forEach {
            it.target.player.sendBlockChange(it.block.location, it.block.blockData)
        }
    }

    // 戦闘中のみ呼び出し。プレイヤーが戦闘中に抜けた際にターゲットを更新
    fun updateTargets(joinedPlayers: MutableSet<BattlePlayer>): Boolean {
        attackTarget = joinedPlayers.shuffled().firstOrNull() ?: return false
        return true
    }

    fun awake(spawnLocation: Location, players: Set<BattlePlayer>) {
        entity = spawnLocation.world!!.spawn(spawnLocation, ArmorStand::class.java) {
            it.run {
                isVisible = false
                setBasePlate(false)
                setArms(true)
                isMarker = true
                isInvulnerable = true
                canPickupItems = false
                setGravity(false)
                isCustomNameVisible = false
                isSmall = true
                this.setHelmet(monster.getIcon())
            }
        }
        players.forEach {
            bossBar.addPlayer(it.player)

            val client = monster.getBookClient(it.player)
            if (client == null) {
                createMonsterBookElement(it.player)
            }
        }
        BattleBars.AWAKE(monster.parameter.health, monster, locale).show(bossBar)

        players.map { it.player }.forEach { player ->
            // TODO implements
//            if (!SoulMonster.VILLAGER.isDefeatedBy(player)) {
            BattleMessages.FIRST_AWAKE.sendTo(player)
//            }
        }

        state = SoulMonsterState.MOVE
        location = entity.location
        destination = ai.searchDestination(chunk, attackTarget, location)
    }

    fun update(elapsedTick: Long) {
        when (state) {
            SoulMonsterState.MOVE -> move(elapsedTick)
            SoulMonsterState.ATTACK -> attack(elapsedTick)
            else -> {
            }
        }
        updateLocation()
        //エフェクト処理が重いため
        // ToDo: 設定項目として設けるべき？
        //MonsterSpiritAnimations.AMBIENT(monster.color).start(entity.eyeLocation)
    }

    private fun updateLocation() {
        val fixedLocation = location.clone().apply {
            if (attackTarget.player.isValid) {
                this.direction = attackTarget.player.eyeLocation.clone()
                        .subtract(eyeLocation.clone())
                        .toVector().normalize()
                val diffY = attackTarget.player.eyeLocation.y - eyeLocation.y
                val distance = attackTarget.player.eyeLocation.distance(eyeLocation)
                val sin = diffY / distance
                val cos = 1.0 - Math.pow(sin, 2.0)
                val diffX = distance * cos
                val rad = Math.atan2(diffY, diffX)
                entity.headPose = EulerAngle(-rad, 0.0, 0.0)
            }
        }
        entity.teleport(fixedLocation)
    }

    private fun move(elapsedTick: Long) {
        val des = destination ?: error("destination must not be null")
        val diff = des.clone().subtract(location).toVector().normalize().multiply(0.1)
        location.add(diff)


        if (elapsedTick.minus(lastAttackTicks) >= monster.parameter.attackInterval) {
            state = SoulMonsterState.ATTACK
        } else if (des.distance(location) <= 0.1) {
            state = SoulMonsterState.MOVE
            destination = ai.searchDestination(chunk, attackTarget, location)
        }
    }

    private fun copyAttackBlocks() = attackBlocks.toSet()

    private fun attack(elapsedTick: Long) {
        // set attack blocks
        if (monster.parameter.attackTimes > 0) {
            (1..monster.parameter.attackTimes).forEach { index ->
                val shotDelay = index * monster.parameter.shotInterval
                Bukkit.getScheduler().scheduleSyncDelayedTask(Gigantic.PLUGIN, {
                    val attackBlocks = ai.getAttackBlocks(chunk, copyAttackBlocks(), attackTarget, elapsedTick + shotDelay)
                    if (attackBlocks == null) {
                        disappearCount++
                        if (disappearCount > 5 * monster.parameter.attackTimes) {
                            state = SoulMonsterState.DISAPPEAR
                        }
                        return@scheduleSyncDelayedTask
                    }
                    attackBlocks.forEach { attack(it) }
                }, shotDelay)
            }
        }
        val moveDelay = monster.parameter.attackTimes * 10L + 20L
        Bukkit.getScheduler().scheduleSyncDelayedTask(Gigantic.PLUGIN, {
            state = SoulMonsterState.MOVE
            destination = ai.searchDestination(chunk, attackTarget, location)
            lastAttackTicks = elapsedTick + moveDelay
        }, moveDelay)

        state = SoulMonsterState.WAIT
    }

    private fun selectAttackType(): BlockData {
        val attackTypeList = listOf(
            Bukkit.createBlockData(monster.parameter.attackMaterial),
            Bukkit.createBlockData(Material.PINK_GLAZED_TERRACOTTA),
            Bukkit.createBlockData(Material.PURPLE_GLAZED_TERRACOTTA)
        )
        return if (health <= monster.parameter.health / 2){
            attackTypeList.weightedRandom(listOf(0.15, 0.7, 0.15))
        }else{
            attackTypeList.filter{ it != healBlockData }.random()
        }
    }

    private fun attack(attackBlock: AttackBlock) {
        val player = attackBlock.target.player
        val block = attackBlock.block
        val selectAttackType = selectAttackType()

        if (!entity.isValid || !player.isValid) return
        // send attack ready particle

        // effects
        SoulMonsterSounds.ATTACK_READY.play(entity.eyeLocation)
        BattleMonsterAnimations.ATTACK_READY(monster.color).exhaust(entity, block.centralLocation, meanY = 0.9)

        attackBlocks.add(attackBlock)

        runTaskTimer(20L, 1L) { ticks ->
            if (ticks > 60L) {
                if (player.isOnline) {
                    player.sendBlockChange(block.location, block.blockData)
                }
                return@runTaskTimer false
            }

            if (!entity.isValid || !player.isValid) return@runTaskTimer true

            if (block.isAir || !attackBlocks.contains(attackBlock)) {
                player.sendBlockChange(block.location, block.blockData)
                return@runTaskTimer true
            }

            // effects
            if ((ticks % 5).toInt() == 0) {
                when (selectAttackType) {
                    attackBlockData -> {
                        BattleMonsterAnimations.ATTACK_READY_BLOCK.start(block.centralLocation)
                    }
                    healBlockData -> {
                        BattleMonsterAnimations.SELF_HEAL_READY_BLOCK.start(block.centralLocation)
                    }
                    debuffBlockData -> {
                        BattleMonsterAnimations.DEBUFF_READY_BLOCK.start(block.centralLocation)
                    }
                }
            }

            player.sendBlockChange(block.location, selectAttackType)
            if (ticks < 45) {
                if (ticks % 20 == 0L) {
                    SoulMonsterSounds.ATTACK_READY_SUB1.play(block.centralLocation)
                }
            }else{
                if (ticks % 4 == 0L && ticks <= 56L) {
                    SoulMonsterSounds.ATTACK_READY_SUB2.play(block.centralLocation)
                }
            }
            return@runTaskTimer true
        }
        // attack
        Bukkit.getScheduler().scheduleSyncDelayedTask(Gigantic.PLUGIN, {
            if (!entity.isValid || !player.isValid || player.isDead) return@scheduleSyncDelayedTask

            if (!attackBlocks.remove(attackBlock)) return@scheduleSyncDelayedTask
            if (block.isAir) return@scheduleSyncDelayedTask

            // TODO implements
            /*player.manipulate(CatalogPlayerCache.HEALTH) { health ->
                health.decrease(monster.parameter.power)
                if (health.isZero) {
                    player.offer(Keys.DEATH_MESSAGE, DeathMessages.BY_MONSTER(player.name, monster))
                }
                PlayerSounds.INJURED.play(player.location)
                BattleMessages.DAMAGE(monster, monster.parameter.power).sendTo(player)
            }

            PlayerMessages.HEALTH_DISPLAY(player.wrappedHealth, player.wrappedMaxHealth).sendTo(player)*/

            // TODO implements
//            if (!SoulMonster.ZOMBIE_VILLAGER.isDefeatedBy(player)) {
            BattleMessages.FIRST_DAMAGE.sendTo(player)
//            }

            // effects
            when (selectAttackType) {
                attackBlockData -> {
                    block.type = Material.AIR
                    val world = block.world
                    world.createExplosion(block.location,2f)
                    block.update()
                    player.damage(monster.parameter.attackDamage.toDouble(), entity)
                }
                healBlockData -> {
                    block.type = Material.AIR
                    block.update()
                    //todo: monster.parameterに移してモンスターごとに変えるべき
                    val healValue = 15
                    if (health + healValue > monster.parameter.health){
                        health = monster.parameter.health
                    }else{
                        health += healValue
                    }
                    BattleBars.AWAKE(health, monster, locale).show(bossBar)
                    SoulMonsterSounds.MONSTER_HEAL.play(player.location)
                }
                debuffBlockData -> {
                    block.type = Material.AIR
                    block.update()
                    val (effectType,level) = debuffList.random()
                    player.addPotionEffect(
                        PotionEffect(
                            effectType,
                            20 * 5,
                            level
                        )
                    )
                    SoulMonsterSounds.DEBUFF_ATTACK.playOnly(player)
                }
            }
        }, 20L + 60L)

    }

    fun defencedByPlayer(block: Block) {
        if (!attackBlocks.removeIf { block == it.block }) return
        SoulMonsterSounds.DEFENCE.play(block.centralLocation)
        BattleMonsterAnimations.DEFENCE(monster.color).absorb(entity, block.centralLocation, meanY = 0.9)
    }

    fun damageByPlayer(player: Player, damage: Long): Long {
        val trueDamage = damage.coerceIn(0L, health)
        totalDamageMap.compute(player) { _, amount ->
            amount?.plus(trueDamage) ?: trueDamage
        }
        health -= trueDamage
        if (health == 0L) {
            state = SoulMonsterState.DEATH
        }
        if (trueDamage > 0L) {
            BattleBars.AWAKE(health, monster, locale).show(bossBar)
            BattleMonsterAnimations.DAMAGE_FROM_PLAYER.start(eyeLocation)
        }
        return trueDamage
    }
    fun win(players: Set<BattlePlayer>){
        players.forEach {
            val player = it.player
            BattleMonsterAnimations.WIN_PARTICLE.start(eyeLocation)
            val client = monster.getBookClient(player)
            if (client != null) {
                updateMonserBookElement(monster, player, true, true)
            } else {
                severe("MonsterBookClient is not found")
            }
        }
    }
    fun lose(player: Player){
        val client = monster.getBookClient(player)
        if (client != null) {
            updateMonserBookElement(monster, player, true, false)
        }else{
            severe("MonsterBookClient is not found")
        }
    }

    private fun createMonsterBookElement(player: Player) {
        val newMonsterBook = MonsterBookClient(
            monsterId = monster.id,
            encounterCount = 0,
            defeatCount = 0,
            firstEncounterDate = DateTime.now(),
            isEligible = false
        )
        player.transform(Keys.MONSTER_BOOK_MAP) {
            it.toMutableMap().apply {
                put(newMonsterBook.monsterId, newMonsterBook)
            }
        }
    }
    private fun updateMonserBookElement(monster: SoulMonster, player: Player, encount: Boolean, win: Boolean){
        val client = player.getOrPut(Keys.MONSTER_BOOK_MAP).values.firstOrNull { it.monsterId == monster.id }
        val affinityCountdown = when(monster.difficultyType){
            SoulMonster.DifficultyType.Easy -> 3
            SoulMonster.DifficultyType.Normal -> 5
            SoulMonster.DifficultyType.Hard -> 7
            else -> {return severe("DifficultyType is not defined")}
        }
        var isChanged = false
        if (client != null) {
            if (encount) {
                client.encounterCount++
                isChanged = true
            }
            if (win) {
                client.defeatCount++
                isChanged = true
            }
            if (client.encounterCount >= affinityCountdown) {
                client.isEligible = true
                isChanged = true
            }
            if (isChanged) {
                player.transform(Keys.MONSTER_BOOK_MAP) {
                    it.toMutableMap().apply {
                        put(client.monsterId, client)
                    }
                }
            }
        }
    }
}