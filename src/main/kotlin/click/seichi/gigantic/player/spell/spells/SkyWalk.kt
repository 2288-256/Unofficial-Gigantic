package click.seichi.gigantic.player.spell.spells

import click.seichi.gigantic.Gigantic
import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.cache.manipulator.catalog.CatalogPlayerCache
import click.seichi.gigantic.config.Config
import click.seichi.gigantic.config.DebugConfig
import click.seichi.gigantic.extension.*
import click.seichi.gigantic.message.messages.PlayerMessages
import click.seichi.gigantic.player.Defaults
import click.seichi.gigantic.player.Invokable
import org.bukkit.Tag
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Fence
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.util.function.Consumer

/**
 * @author tar0ss
 */
object SkyWalk : Invokable {
    override fun findInvokable(player: Player): Consumer<Player>? {
        return Consumer { p ->
            // 前回設置したガラスブロック
            val prevSet = p.getOrPut(Keys.SPELL_SKY_WALK_PLACE_BLOCKS)
            // もし続行不可能なら以前のガラスブロックを削除しておく
            if (p.gameMode != GameMode.SURVIVAL ||
                p.isSneaking ||
                !p.getOrPut(Keys.SPELL_SKY_WALK_TOGGLE) ||
                !player.hasMana()
            ) {
                val location = p.location.clone()
                location.y -= 1
                //フェンスにめり込まないように
                if (p.isSneaking && location.block.type == Defaults.SKY_WALK_FENCE_MATERIAL) return@Consumer
                Gigantic.USE_BLOCK_SET.removeAll(prevSet)
                prevSet.forEach { block ->
                    revert(block)
                }
                p.offer(Keys.SPELL_SKY_WALK_PLACE_BLOCKS, setOf())
                return@Consumer
            }

            val placeBlockSet = calcPlaceBlockSet(p)
            // 以前の不要なブロックを削除
            prevSet.filterNot {
                placeBlockSet.contains(it)
            }.apply {
                Gigantic.USE_BLOCK_SET.removeAll(this)
            }.forEach { block ->
                revert(block)
            }

            // 足場設置
            placeBlockSet.filterNot {
                prevSet.contains(it)
            }.apply {
                if (size == 0) return@apply
                val consumeMana = calcConsumeMana(size)
                if (!Gigantic.IS_DEBUG || !DebugConfig.SPELL_INFINITY) {
                    p.manipulate(CatalogPlayerCache.MANA) {
                        it.decrease(consumeMana)
                    }
                }
                if (consumeMana > BigDecimal.ZERO) {
                    PlayerMessages.MANA_DISPLAY(p.mana, p.maxMana).sendTo(p)
                }
                forEach { block ->
                    replace(block)
                }
            }
            // 設置ブロックを保存
            p.offer(Keys.SPELL_SKY_WALK_PLACE_BLOCKS, placeBlockSet)
            // globalでも保存
            Gigantic.USE_BLOCK_SET.addAll(placeBlockSet)

        }
    }

    fun revert(block: Block) {
        // 凝固優先のため、もし凝固したブロックがあれば除外
        if (block.isCondensedWaters || block.isCondensedLavas) return

        // ブロックのタイプを変更
        block.type = when (block.type) {
            Defaults.SKY_WALK_WATER_MATERIAL -> Material.WATER
            Defaults.SKY_WALK_LAVA_MATERIAL -> Material.LAVA
            Defaults.SKY_WALK_TORCH_MATERIAL -> Material.TORCH
            Defaults.SKY_WALK_FENCE_MATERIAL -> Material.OAK_FENCE
            else -> Material.AIR
        }

        // ブロックがフェンスである場合、接続状態を設定
        if (block.type == Material.OAK_FENCE) {
            updateFenceConnectionsWithBlockData(block)
        }
    }


    //とある条件下ではフェンスの接続が正常に行われない
    fun updateFenceConnectionsWithBlockData(block: Block) {
        val directions = listOf(
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST
        )

        // ブロックのBlockDataを取得し、Fenceとしてキャスト
        val fenceData = block.blockData as Fence

        for (face in directions) {
            val neighbor = block.getRelative(face)

            // 隣接ブロックがフェンスまたは接続可能なブロックなら接続を設定
            if (isConnectable(neighbor)) {
                fenceData.setFace(face, true)
            } else {
                fenceData.setFace(face, false)
            }
        }

        // 更新したBlockDataをブロックに適用
        block.blockData = fenceData
    }

    fun isConnectable(block: Block): Boolean {
        // フェンスまたは接続可能なブロックの条件を設定
        return block.type == Material.OAK_FENCE || block.type.isSolid
    }

    fun replace(block: Block) {
        block.type = when {
            block.isWater -> Defaults.SKY_WALK_WATER_MATERIAL
            block.isLava -> Defaults.SKY_WALK_LAVA_MATERIAL
            block.type == Material.TORCH -> Defaults.SKY_WALK_TORCH_MATERIAL
            Tag.FENCES.isTagged(block.type) -> Defaults.SKY_WALK_FENCE_MATERIAL
            else -> Defaults.SKY_WALK_AIR_MATERIAL
        }
        block.world.playSound(block.centralLocation, Sound.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 0.2F, 0.5F)
    }

    fun calcConsumeMana(num: Int) = num.times(Config.SPELL_SKY_WALK_MANA_PER_BLOCK)
        .toBigDecimal()

    fun calcPlaceBlockSet(player: Player): Set<Block> {
        val prevSet = player.getOrPut(Keys.SPELL_SKY_WALK_PLACE_BLOCKS)
        val pLocBlock = player.location.block
        val base = pLocBlock.getRelative(BlockFace.DOWN)

        val columnSet = mutableSetOf(base)
        (1..Config.SPELL_SKY_WALK_RADIUS).forEach { length ->
            columnSet.add(base.getRelative(BlockFace.NORTH, length))
            columnSet.add(base.getRelative(BlockFace.SOUTH, length))
        }
        val allSet = mutableSetOf(*columnSet.toTypedArray())
        (1..Config.SPELL_SKY_WALK_RADIUS).forEach { length ->
            columnSet.forEach { columnBlock ->
                allSet.add(columnBlock.getRelative(BlockFace.EAST, length))
                allSet.add(columnBlock.getRelative(BlockFace.WEST, length))
            }
        }
        val additiveSet = prevSet.filter { allSet.contains(it) }.toSet()
        return allSet.filter { it.isPassable || it.isAir || Tag.FENCES.isTagged(it.type) }
            // 水と溶岩も固めるために除外
//                    .filterNot { it.isWater || it.isLava }
            .filterNot { it.isSpawnArea }
            .filterNot { it.y == 0 }
            .filterNot { Gigantic.USE_BLOCK_SET.contains(it) }
            .toMutableSet().apply {
                addAll(additiveSet)
            }.toSet()
    }
}
