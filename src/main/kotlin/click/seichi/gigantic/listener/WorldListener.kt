package click.seichi.gigantic.listener

import click.seichi.gigantic.Gigantic
import click.seichi.gigantic.cache.PlayerCacheMemory
import click.seichi.gigantic.config.Config
import click.seichi.gigantic.extension.runTaskLaterAsync
import click.seichi.gigantic.player.Defaults
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.event.world.WorldSaveEvent

/**
 * @author tar0ss
 */
class WorldListener : Listener {

    @EventHandler
    fun onWorldInit(event: WorldInitEvent) {
        val world = event.world
        world.setSpawnFlags(false, false)
        world.pvp = false
        world.difficulty = Difficulty.NORMAL
        world.isAutoSave = true
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true)
        world.setGameRule(GameRule.DO_FIRE_TICK, false)
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, false)
        world.setGameRule(GameRule.SPAWN_RADIUS, 40)
        world.worldBorder.setCenter(0.0, 0.0)
        world.worldBorder.size = Config.WORLD_SIDE_LENGTH
        world.worldBorder.warningDistance = 0
        world.worldBorder.warningTime = 0
    }

    @EventHandler
    fun onWorldSave(event: WorldSaveEvent) {
        if (event.world.environment == org.bukkit.World.Environment.NORMAL) {
            // プレイヤーデータの逐次保存
            val onlineIdSet = Bukkit.getOnlinePlayers().map { it.uniqueId }.toSet()

            if (onlineIdSet.isEmpty()) return
            runTaskLaterAsync(Defaults.PLAYER_DATA_SAVE_DELAY) {
            onlineIdSet.forEach { uniqueId ->
                // 保存処理
                try {
                PlayerCacheMemory.write(uniqueId)
                } catch (e: Exception) {
                e.printStackTrace()
                }
            }
            }
            // ランキングデータの更新
            runTaskLaterAsync(Defaults.RANK_DATA_SAVE_DELAY) {
            Gigantic.PLUGIN.updateRanking()
            }
        }
    }

}