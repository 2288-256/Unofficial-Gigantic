package click.seichi.gigantic.listener

import click.seichi.gigantic.Gigantic
import click.seichi.gigantic.acheivement.Achievement
import click.seichi.gigantic.animation.PlayerAnimations
import click.seichi.gigantic.belt.Belt
import click.seichi.gigantic.cache.PlayerCacheMemory
import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.cache.manipulator.MineBlockReason
import click.seichi.gigantic.cache.manipulator.catalog.CatalogPlayerCache
import click.seichi.gigantic.config.Config
import click.seichi.gigantic.config.PlayerLevelConfig
import click.seichi.gigantic.event.events.LevelUpEvent
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.menu.Menu
import click.seichi.gigantic.message.messages.PlayerMessages
import click.seichi.gigantic.player.ExpProducer
import click.seichi.gigantic.popup.PlayerPops
import click.seichi.gigantic.sound.sounds.PlayerSounds
import click.seichi.gigantic.sound.sounds.SkillSounds
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToLong


/**
 * @author tar0ss
 */
class PlayerListener : Listener {

    // スポーン付近を破壊した場合問答無用でキャンセル
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onSpawnAreaBlockBreak(event: BlockBreakEvent) {
        val player = event.player ?: return
        val block = event.block ?: return
        val spawnLocation = block.world.spawnLocation
        val spawnRadius = block.world.getGameRuleValue(GameRule.SPAWN_RADIUS) ?: 32
        if (player.gameMode == GameMode.CREATIVE) return
        if (abs(block.x - spawnLocation.x) >= spawnRadius ||
                abs(block.z - spawnLocation.z) >= spawnRadius) return
        PlayerMessages.SPAWN_PROTECT.sendTo(player)
        event.isCancelled = true
    }

    // プレイヤーデータのロード
    @EventHandler
    fun onPlayerPreLoginAsync(event: AsyncPlayerPreLoginEvent) {
        runBlocking {
            /**
             * 複数サーバで動かすと，ログアウト時の書き込みよりもログイン時の読込の方が早くなってしまい，
             * データが消失するので，ある程度余裕を持って３秒delay．
             * このためだけのcolumn用意するべきかも
             */
            delay(TimeUnit.SECONDS.convert(3L, TimeUnit.MILLISECONDS))
            PlayerCacheMemory.add(event.uniqueId, event.name)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player ?: return
        if (!PlayerCacheMemory.contains(player.uniqueId)) {
            return
        }


        if (player.gameMode == GameMode.SPECTATOR) {
            player.find(CatalogPlayerCache.AFK_LOCATION)?.getLocation()?.let {
                player.teleport(it)
            }
            player.gameMode = GameMode.SURVIVAL
        }
        val uniqueId = player.uniqueId
        PlayerCacheMemory.remove(uniqueId, true)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player ?: return
        /**
         * この処理がないと、cacheがロードされずに参加できてしまう
         */
        if (!PlayerCacheMemory.contains(player.uniqueId)) {
            player.kickPlayer("Server is starting...Please wait a few seconds.")
            return
        }

        if (!player.isOp) player.gameMode = GameMode.SURVIVAL

        player.manipulate(CatalogPlayerCache.LEVEL) { level ->
            level.calculate(ExpProducer.calcExp(player)) {}
            PlayerMessages.EXP_BAR_DISPLAY(level).sendTo(player)
        }

        player.manipulate(CatalogPlayerCache.MANA) {
            it.updateMaxMana()
        }

        player.manipulate(CatalogPlayerCache.HEALTH) {
            it.updateMaxHealth()
            PlayerMessages.HEALTH_DISPLAY(it).sendTo(player)
        }
        player.saturation = Float.MAX_VALUE
        player.foodLevel = 20
        // 4秒間無敵付与
        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
                80,
                5,
                false,
                false
        ))

        Achievement.update(player, true)

        player.getOrPut(Keys.BELT).wear(player)
        player.getOrPut(Keys.BAG).carry(player)


        player.updateInventory()

        PlayerMessages.MEMORY_SIDEBAR(
                player.find(CatalogPlayerCache.MEMORY) ?: return,
                player.find(CatalogPlayerCache.APTITUDE) ?: return,
                true
        ).sendTo(player)
    }

    // プレイヤーのメニュー以外のインベントリーオープンをキャンセル
    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (event.player !is Player) return
        if (event.player.gameMode != GameMode.SURVIVAL) return
        if (event.inventory.holder is Menu) return
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerArmorStandManipulate(event: PlayerArmorStandManipulateEvent) {
        if (event.player.gameMode != GameMode.SURVIVAL) return
        event.isCancelled = true
    }

    // プレイヤーの全てのドロップをキャンセル
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        if (event.player.gameMode != GameMode.SURVIVAL) return
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerItemHeld(event: PlayerItemHeldEvent) {
        val player = event.player ?: return
        if (player.gameMode != GameMode.SURVIVAL) return
        val belt = player.getOrPut(Keys.BELT)
        belt.getHotButton(event.newSlot)?.onItemHeld(player, event)
        if (belt.hasFixedSlot() && !belt.isFixed(event.newSlot))
            event.isCancelled = true
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player ?: return
        if (player.gameMode != GameMode.SURVIVAL) return
        val belt = player.getOrPut(Keys.BELT)
        if (event.action == Action.PHYSICAL) return
        belt.findFixedButton()?.onInteract(player, event)
        belt.offHandButton?.onInteract(player, event)
    }

    @EventHandler
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        val player = event.player ?: return
        event.isCancelled = true
        var current: Belt? = null
        player.manipulate(CatalogPlayerCache.BELT_SWITCHER) {
            current = it.current
            it.switch()
        }
        val nextBelt = player.getOrPut(Keys.BELT)
        if (current == nextBelt) return
        nextBelt.wear(player)
        SkillSounds.SWITCH.playOnly(player)
    }

    @EventHandler
    fun onLevelUp(event: LevelUpEvent) {
        val player = event.player

        PlayerMessages.LEVEL_UP_LEVEL(event.level).sendTo(player)
        PlayerMessages.LEVEL_UP_TITLE(event.level).sendTo(player)
        PlayerPops.LEVEL_UP.follow(player, meanY = 3.7)
        PlayerAnimations.LAUNCH_FIREWORK.start(player.location)
        PlayerSounds.LEVEL_UP.play(player.location)

        Achievement.update(player)

        if (Achievement.MANA_STONE.isGranted(player)) {
            player.manipulate(CatalogPlayerCache.MANA) {
                val prevMax = it.max
                it.updateMaxMana()
                it.increase(it.max, true)
                PlayerMessages.MANA_DISPLAY(it).sendTo(player)
                if (prevMax == it.max) return@manipulate
                PlayerMessages.LEVEL_UP_MANA(prevMax, it.max).sendTo(player)
            }
        }

        player.manipulate(CatalogPlayerCache.HEALTH) {
            val prevMax = it.max
            it.updateMaxHealth()
            it.increase(it.max)
            PlayerMessages.HEALTH_DISPLAY(it).sendTo(player)
            if (prevMax == it.max) return@manipulate
            PlayerMessages.LEVEL_UP_HEALTH(prevMax, it.max).sendTo(player)
        }

        player.getOrPut(Keys.BELT).wear(player)
        player.getOrPut(Keys.BAG).carry(player)

        player.updateInventory()
    }

    @EventHandler
    fun onChangeFoodLevel(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        if (event.cause == EntityDamageEvent.DamageCause.STARVATION) {
            event.isCancelled = true
            return
        }
        if (event.cause == EntityDamageEvent.DamageCause.SUICIDE)
            event.damage = Double.MAX_VALUE
    }

    @EventHandler
    fun onRegainHealth(event: EntityRegainHealthEvent) {
        if (event.entity !is Player) return
        if (event.regainReason == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity ?: return
        event.keepInventory = true
        event.keepLevel = true
        // asSafetyを使うとnullの除外ができないのでこのまま
        player.getOrPut(Keys.DEATH_MESSAGE)?.asSafety(player.wrappedLocale)?.let { deathMessage ->
            event.deathMessage = deathMessage
        }
        player.offer(Keys.DEATH_MESSAGE, null)

        player.manipulate(CatalogPlayerCache.LEVEL) { level ->
            player.manipulate(CatalogPlayerCache.MINE_BLOCK) {
                val expToCurrentLevel = PlayerLevelConfig.LEVEL_MAP[level.current] ?: 0L
                val expToNextLevel = PlayerLevelConfig.LEVEL_MAP[level.current + 1] ?: 0L
                val maxPenalty = level.exp.minus(expToCurrentLevel)
                val penaltyMineBlock = expToNextLevel.minus(expToCurrentLevel)
                        .div(100L)
                        .times(Config.PLAYER_DEATH_PENALTY.times(100).roundToLong())
                        .coerceAtMost(maxPenalty)
                it.add(penaltyMineBlock, MineBlockReason.DEATH_PENALTY)
                if (penaltyMineBlock != 0L)
                    PlayerMessages.DEATH_PENALTY(penaltyMineBlock).sendTo(player)
            }
            level.calculate(ExpProducer.calcExp(player)) {}
            PlayerMessages.EXP_BAR_DISPLAY(level).sendTo(player)
        }
    }

    @EventHandler
    fun onReSpawn(event: PlayerRespawnEvent) {
        val player = event.player ?: return
        Bukkit.getServer().scheduler.runTaskLater(Gigantic.PLUGIN, {
            if (!player.isValid) return@runTaskLater
            player.manipulate(CatalogPlayerCache.HEALTH) {
                it.increase(it.max.div(10.0).times(3.0).toLong())
            }
            PlayerMessages.HEALTH_DISPLAY(player.find(CatalogPlayerCache.HEALTH) ?: return@runTaskLater
            ).sendTo(player)
        }, 1L)

    }

    @EventHandler
    fun onChangeGameMode(event: PlayerGameModeChangeEvent) {
        when (event.newGameMode) {
            GameMode.SURVIVAL -> {
                val belt = event.player.getOrPut(Keys.BELT)
                belt.getFixedSlot()?.let {
                    event.player.inventory.heldItemSlot = it
                }
                belt.wear(event.player)
                event.player.getOrPut(Keys.BAG).carry(event.player)
            }
            GameMode.CREATIVE -> {
                event.player.inventory.clear()
                event.player.updateInventory()
            }
            else -> {
            }
        }
    }

    @EventHandler
    fun onPlaceBlock(event: BlockPlaceEvent) {
        val player = event.player ?: return
        if (player.gameMode != GameMode.SURVIVAL) return
        event.isCancelled = true
    }

    @EventHandler
    fun onMultiPlaceBlock(event: BlockMultiPlaceEvent) {
        val player = event.player ?: return
        if (player.gameMode != GameMode.SURVIVAL) return
        event.isCancelled = true
    }

}