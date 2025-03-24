package click.seichi.gigantic.monster

import click.seichi.gigantic.cache.key.Keys
import click.seichi.gigantic.extension.getOrPut
import click.seichi.gigantic.extension.transform
import click.seichi.gigantic.head.Head
import click.seichi.gigantic.message.LocalizedText
import click.seichi.gigantic.monster.ai.SoulMonsterAI
import click.seichi.gigantic.monster.parameter.SoulMonsterParameter
import click.seichi.gigantic.relic.Relic
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * @author tar0ss
 */
enum class SoulMonster(
        val id: Int,
        private val head: Head?,
        val difficultyType: DifficultyType,
        private val localizedName: LocalizedText,
        private val localizedLore: List<LocalizedText>?,
        val color: Color,
        val parameter: SoulMonsterParameter,
        private val aiClass: KClass<SoulMonsterAI>,

    enum class DifficultyType(val displayName: LocalizedText, val material: Material) {
        All(MonsterDifficultyMessages.ALL, Material.DIAMOND_SWORD) {
            override fun isContain(player: Player, monster: SoulMonster): Boolean {
                return true
            }
        },
        Easy(MonsterDifficultyMessages.EASY, Material.COAL) {
            override fun isContain(player: Player, monster: SoulMonster): Boolean {
                return monster.difficultyType == Easy
            }
        },
        Normal(MonsterDifficultyMessages.NORMAL, Material.IRON_INGOT) {
            override fun isContain(player: Player, monster: SoulMonster): Boolean {
                return monster.difficultyType == Normal
            }
        },
        Hard(MonsterDifficultyMessages.HARD, Material.GOLD_INGOT) {
            override fun isContain(player: Player, monster: SoulMonster): Boolean {
                return monster.difficultyType == Hard
            }
        }
        ;
        abstract fun isContain(player: Player, monster: SoulMonster): Boolean
    }

    companion object {
        // 重複確認
        val hasDuplicateId = values().size != values().map { it.id }.toSet().size
    }

    fun getIcon() = head?.toItemStack() ?: ItemStack(Material.ZOMBIE_HEAD)

    fun getName(locale: Locale) = localizedName.asSafety(locale)

    fun getLore(locale: Locale) = localizedLore?.map { it.asSafety(locale) }

    val dropRelicSet = dropRelic.toSet()

    fun createAIInstance() = aiClass.createInstance()

    fun isDefeatedBy(player: Player) = Keys.SOUL_MONSTER[this]?.let { player.getOrPut(it) > 0 } ?: false

    fun defeatedBy(player: Player) {
        player.transform(Keys.SOUL_MONSTER[this] ?: return) { it + 1 }
    }

    fun getBookClient(player: Player): MonsterBookClient? {
        val monsterBookMap = player.getOrPut(Keys.MONSTER_BOOK_MAP)
        val monsterKey = this.id
        return monsterBookMap[monsterKey]
    }

    data class DropRelic(
            val relic: Relic,
            val probability: Double = 1.0
    )
}